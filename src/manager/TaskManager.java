package manager;

import task.*;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idNumber = 0;

    private int id(Task task) {
        task.setId(++idNumber);
        return idNumber;
    }

    public void saveTask(Task task) {
        tasks.put(id(task), task);
    }

    public void saveEpic(Epic epic) {
        epics.put(id(epic), epic);
        changeEpicStatus(epic);
    }

    public void saveSubtask(Subtask subtask) {
        int subtaskId = subtask.getEpicId();
        Epic epic = epics.get(subtaskId);
        if (epic != null) {
            subtasks.put(id(subtask), subtask);
            epic.addSubtaskId(subtask);
            changeEpicStatus(epic);
        }
    }

    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtask() {
        for (Integer sub : subtasks.keySet()) {
            Subtask subtask = subtasks.get(sub);
            if (subtask != null) {
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.getSubtask().clear();
                    changeEpicStatus(epic);
                }
            }
        }
        subtasks.clear();
    }

    public Task getTaskByIdNumber(int idNumber) {
        return tasks.get(idNumber);
    }

    public Epic getEpicByIdNumber(int idNumber) {
        return epics.get(idNumber);
    }

    public Subtask getSubtaskByIdNumber(int idNumber) {
        return subtasks.get(idNumber);
    }

    public Task createTask(Task task) {
        return new Task(task.getName(), task.getDescription(), task.getStatus());
    }

    public Epic createEpic(Epic epic) {
        return new Epic(epic.getName(), epic.getDescription(), epic.getStatus());
    }

    public Subtask createSubtask(Subtask subtask) {
        return new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
    }

    public void updateTask(Task task) {
        int idUpdatedTask = task.getId();
        if (tasks.containsKey(idUpdatedTask)) {
            tasks.put(idUpdatedTask, task);
        }
    }

    public void updateEpic(Epic epic) {

        int idUpdatedEpic = epic.getId();
        Status currentEpicStatus = epics.get(idUpdatedEpic).getStatus();
        Epic newEpic = new Epic(epic.getId(), epic.getName(), epic.getDescription(), currentEpicStatus
                , epic.getSubtask());
        if (epics.containsKey(idUpdatedEpic)) {
            epics.put(idUpdatedEpic, newEpic);
            changeEpicStatus(newEpic);
        }
    }

    public void updateSubtask(Subtask subtask) {

        int idUpdatedSubTask = subtask.getId();
        if (subtasks.containsKey(idUpdatedSubTask)) {
            subtasks.put(idUpdatedSubTask, subtask);
        }
        int epicIdForStatus = subtask.getEpicId();
        Epic epic = epics.get(epicIdForStatus);
        if (epic != null) {
            changeEpicStatus(epic);
        }
    }

    public void deleteTaskById(int idNumber) {
        tasks.remove(idNumber);
    }

    public void deleteEpicById(int idNumber) {
        Epic epic = epics.get(idNumber);
        if (epic != null) {
            for (int sub : epic.getSubtask()) {
                subtasks.remove(sub);
            }
        }
        epics.remove(idNumber);
    }

    public void deleteSubtaskById(int idNumber) {
        Subtask sub = subtasks.get(idNumber);
        int epicId = sub.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.getSubtask().remove(Integer.valueOf(idNumber));
            changeEpicStatus(epic);
        }
        subtasks.remove(idNumber);
    }

    public ArrayList<Subtask> subtaskList(int idNumber) {
        ArrayList<Subtask> listSubtasks = new ArrayList<>();
        for (int subtaskIdNumber : subtasks.keySet()) {
            Subtask subtask = subtasks.get(subtaskIdNumber);
            if (subtask != null && idNumber == subtask.getEpicId()) {
                listSubtasks.add(subtask);
            }
        }
        return listSubtasks;
    }

    private void changeEpicStatus(Epic epic) {
        int epicID = epic.getId();
        ArrayList<Subtask> updateListOfSubtasks = subtaskList(epicID);

        int doneCounter = 0;
        int newCounter = 0;
        for (Subtask subtask : updateListOfSubtasks) {
            switch (subtask.getStatus()) {
                case NEW:
                    newCounter++;
                    break;
                case IN_PROGRESS:
                    break;
                case DONE:
                    doneCounter++;
                    break;
            }
        }

        if ((updateListOfSubtasks.size() == 0) || (newCounter == updateListOfSubtasks.size())) {
            epic.setStatus(Status.NEW);
        } else if (doneCounter == updateListOfSubtasks.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
