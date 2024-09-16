package manager;


import exceptions.ManagerSaveException;
import task.*;

import java.io.*;

import java.nio.file.Files;
import java.util.*;


public class FileBackedTasksManager extends InMemoryTaskManager {


    private File file = new File("history.csv");

    public FileBackedTasksManager(File file) {
        this.file = file;
    }


    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public Task getTaskByIdNumber(int id) {
        Task task = super.getTaskByIdNumber(id);
        save();
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public Epic getEpicByIdNumber(int id) {
        Epic epic = super.getEpicByIdNumber(id);
        save();
        return epic;
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtask() {
        super.deleteSubtask();
        save();
    }

    @Override
    public Subtask getSubtaskByIdNumber(int id) {
        Subtask subtask = super.getSubtaskByIdNumber(id);
        save();
        return subtask;
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    public void save() {
        try (FileWriter File = new FileWriter(file)) {
            File.write("id,type,name,status,description,epic\n");
            for (Task task : getTasksList()) {
                File.write(taskToString(task) + "\n");
            }
            for (Epic epic : getEpicsList()) {
                File.write(taskToString(epic) + "\n");
            }
            for (Subtask subtask : getSubtaskList()) {
                File.write(taskToString(subtask) + subtask.getEpicId() + "\n");
            }
            File.write("\n");
            File.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public void loadFromFile(File file) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(file);
        try {
            String csv = Files.readString(file.toPath());
            String[] lines = csv.split("\n");
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (!line.isEmpty()) {
                    Task task = stringToTask(line);
                    if (task != null) {
                        switch (task.getType()) {
                            case TASK:
                                taskManager.addTaskInternal(task);
                                break;
                            case EPIC:
                                taskManager.addEpicInternal((Epic) task);
                                break;
                            case SUBTASK:
                                Subtask subtask = (Subtask) task;
                                if (taskManager.epics.containsKey(subtask.getEpicId())) {
                                    taskManager.addSubtaskInternal(subtask);
                                }
                                break;
                        }
                    }
                    restoreIdNumber();
                } else {
                    List<Integer> history = historyFromStringToList(lines[i + 1]);
                    convertRestoredListOfHistoryInHistoryManager(history, taskManager);
                    break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private void restoreIdNumber() {
        Map<Integer, Task> allTasks = new HashMap<>();
        allTasks.putAll(this.tasks);
        allTasks.putAll(this.epics);
        allTasks.putAll(this.subtasks);
        int maxId = 0;
        for (Map.Entry<Integer, Task> entry : allTasks.entrySet()) {
            int id = entry.getValue().getId();
            maxId = Integer.max(maxId, id);
        }
        this.idNumber = maxId;
    }

    private static void convertRestoredListOfHistoryInHistoryManager(List<Integer> restoredHistory,
                                                                     FileBackedTasksManager manager) {
        if (!restoredHistory.isEmpty()) {
            for (Integer id : restoredHistory) {
                Task task = manager.getTaskByIdNumber(id);
                if (task != null) {
                    manager.historyManager.add(task);
                    continue;
                }

                Epic epic = manager.getEpicByIdNumber(id);
                if (epic != null) {
                    manager.historyManager.add(epic);
                    continue;
                }

                Subtask subtask = manager.getSubtaskByIdNumber(id);
                if (subtask != null) {
                    manager.historyManager.add(subtask);
                }
            }
        }
    }


    private static String taskToString(Task task) {
        if (task != null) {
            return String.format("%d,%s,%s,%s,%s,", task.getId(),
                    typeOfTaskToString(task.getType()), task.getName(),
                    typeOfStatusToString(task.getStatus()), task.getDescription());
        }
        return "";
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder viewedIds = new StringBuilder();
        for (Task task : manager.getHistory()) {
            viewedIds.append(task.getId()).append(",");
        }
        return viewedIds.toString();
    }

    private static Task stringToTask(String line) {
        if (!line.equals(" ") && !line.equals("")) {
            String[] parameters = line.split(",");
            TaskType type = TaskType.valueOf(parameters[1]);
            switch (type) {
                case TASK:
                    Task task = new Task();
                    task.setId(Integer.parseInt(parameters[0]));
                    task.setName(parameters[2]);
                    task.setStatus(Status.valueOf(parameters[3]));
                    task.setDescription(parameters[4]);
                    return task;
                case EPIC:
                    Epic epic = new Epic();
                    epic.setId(Integer.parseInt(parameters[0]));
                    epic.setName(parameters[2]);
                    epic.setStatus(Status.valueOf(parameters[3]));
                    epic.setDescription(parameters[4]);
                    return epic;
                case SUBTASK:
                    if (parameters.length > 4) {
                        Subtask subtask = new Subtask();
                        subtask.setId(Integer.parseInt(parameters[0]));
                        subtask.setName(parameters[2]);
                        subtask.setStatus(Status.valueOf(parameters[3]));
                        subtask.setDescription(parameters[4]);
                        subtask.setEpicId(Integer.parseInt(parameters[5]));
                        return subtask;
                    } else {
                        break;
                    }
            }

        }
        return null;
    }

    private static List<Integer> historyFromStringToList(String value) {
        List<Integer> listFilledWithIds = new ArrayList<>();
        if (!value.isEmpty()) {
            String[] listOfViewed = value.split(",");
            for (String s : listOfViewed) {
                listFilledWithIds.add(Integer.parseInt(s));
            }
        }
        return listFilledWithIds;
    }

    private static String typeOfTaskToString(TaskType type) {
        for (TaskType value : TaskType.values()) {
            if (type.equals(value)) {
                return type.toString();
            }
        }
        return "";
    }

    private static String typeOfStatusToString(Status status) {
        for (Status value : Status.values()) {
            if (status.equals(value)) {
                return status.toString();
            }
        }
        return "";
    }


}

