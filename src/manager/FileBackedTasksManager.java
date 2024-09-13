package manager;


import exceptions.ManagerSaveException;
import task.*;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class FileBackedTasksManager extends InMemoryTaskManager {


    private File file;

    private final String path = "history.csv";

    public FileBackedTasksManager() {
    }

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
        try (FileWriter File = new FileWriter(path)) {
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

    public static FileBackedTasksManager loadFromFile(File file) {
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
                                taskManager.addTask(task);
                                break;
                            case EPIC:
                                Epic epic = (Epic) stringToTask(line);
                                taskManager.addEpic(epic);
                                break;
                            case SUBTASK:
                                Subtask subtask = (Subtask) stringToTask(line);
                                Epic subtasksEpic = taskManager.getEpicByIdNumber(subtask.getEpicId());
                                if (subtasksEpic != null) {
                                    taskManager.addSubtask(subtask);
                                }
                                break;
                        }
                    }
                } else {
                    List<Integer> history = historyFromStringToList(lines[i + 1]);
                    convertRestoredListOfHistoryInHistoryManager(history, taskManager);
                    break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        taskManager.save();
        return taskManager;
    }

    private static void convertRestoredListOfHistoryInHistoryManager(List<Integer> restoredHistory,
                                                                     FileBackedTasksManager manager) {
        if (!restoredHistory.isEmpty()) {
            for (Integer integer : restoredHistory) {
                for (Task task : manager.getTasksList()) {
                    if (task.getId() == integer) {
                        manager.historyManager.add(task);
                    }
                }
                for (Epic epic : manager.getEpicsList()) {
                    if (epic.getId() == integer) {
                        manager.historyManager.add(epic);
                    }
                }
                for (Subtask subtask : manager.getSubtaskList()) {
                    if (subtask.getId() == integer) {
                        manager.historyManager.add(subtask);
                    }
                }
            }
        }
    }

    public static String taskToString(Task task) {
        if (task != null) {
            return String.format("%d,%s,%s,%s,%s,", task.getId(),
                    typeOfTaskToString(task.getType()), task.getName(),
                    typeOfStatusToString(task.getStatus()), task.getDescription());
        }
        return "";
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder viewedIds = new StringBuilder();
        for (Task task : manager.getHistory()) {
            viewedIds.append(task.getId()).append(",");
        }
        return viewedIds.toString();
    }

    public static Task stringToTask(String line) {
        if (!line.equals(" ") && !line.equals("")) {
            String[] parameters = line.split(",");
            TaskType type = TaskType.valueOf(parameters[1]);
            for (int i = 0; i < parameters.length; i++) {
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
        }
        return null;
    }

    public static List<Integer> historyFromStringToList(String value) {
        List<Integer> listFilledWithIds = new ArrayList<>();
        if (!value.isEmpty()) {
            String[] listOfViewed = value.split(",");
            for (String s : listOfViewed) {
                listFilledWithIds.add(Integer.parseInt(s));
            }
        }
        return listFilledWithIds;
    }

    public static String typeOfTaskToString(TaskType type) {
        for (TaskType value : TaskType.values()) {
            if (type.equals(value)) {
                return type.toString();
            }
        }
        return "";
    }

    public static String typeOfStatusToString(Status status) {
        for (Status value : Status.values()) {
            if (status.equals(value)) {
                return status.toString();
            }
        }
        return "";
    }


}

