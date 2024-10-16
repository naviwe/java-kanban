package manager;

import com.google.gson.Gson;
import exceptions.ManagerSaveException;
import server.*;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.util.Arrays;

public class HttpTaskManager extends FileBackedTasksManager implements TaskManager {

    final KVTaskClient client;
    final Gson gson = Managers.getGson();

    public HttpTaskManager(String url) throws IOException, InterruptedException {
        super(null);
        client = new KVTaskClient(url);
    }

    public static HttpTaskManager loadFromServer(String url) {
        try {
            HttpTaskManager httpTaskManager = new HttpTaskManager(url);
            KVTaskClient client = new KVTaskClient(url);
            Gson gson = Managers.getGson();

            Task[] taskArray = gson.fromJson(client.load("task"), Task[].class);
            Epic[] epicArray = gson.fromJson(client.load("epic"), Epic[].class);
            Subtask[] subtaskArray = gson.fromJson(client.load("subtask"), Subtask[].class);
            Integer[] historyArray = gson.fromJson(client.load("history"), Integer[].class);

            Arrays.asList(taskArray).forEach(task -> {
                httpTaskManager.tasks.put(task.getId(), task);
                httpTaskManager.updateTask(task);
            });

            Arrays.asList(epicArray).forEach(epic -> httpTaskManager.epics.put(epic.getId(), epic));

            Arrays.asList(subtaskArray).forEach(subtask -> {
                httpTaskManager.subtasks.put(subtask.getId(), subtask);
                httpTaskManager.updateSubtask(subtask);
            });

            Arrays.asList(historyArray).forEach(integer -> {
                if (httpTaskManager.tasks.containsKey(integer)) {
                    Task task = httpTaskManager.tasks.get(integer);
                    httpTaskManager.historyManager.add(task);
                } else if (httpTaskManager.subtasks.containsKey(integer)) {
                    Subtask subtask = httpTaskManager.subtasks.get(integer);
                    httpTaskManager.historyManager.add(subtask);
                } else if (httpTaskManager.epics.containsKey(integer)) {
                    Epic epic = httpTaskManager.epics.get(integer);
                    httpTaskManager.historyManager.add(epic);
                }
            });
            return httpTaskManager;
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка чтения c cервера: " + e.getMessage());
        }
    }

    @Override
    public void save() {
        try {
            String jsonTaskArray = gson.toJson(tasks.values());
            String jsonEpicArray = gson.toJson(epics.values());
            String jsonSubtaskArray = gson.toJson(subtasks.values());
            Integer[] historyArray = getHistory().stream().map(Task::getId).toArray(Integer[]::new);
            String jsonHistoryArray = gson.toJson(historyArray);
            client.put("task", jsonTaskArray);
            client.put("epic", jsonEpicArray);
            client.put("subtask", jsonSubtaskArray);
            client.put("history", jsonHistoryArray);
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка записи на сервер: " + e.getMessage());
        }
    }
}
