package manager;

import task.*;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idNumber = 0;

    private int newId() {
        return ++idNumber;
    }

    public void addTask(Task task) {
        task.setId(newId());
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(newId());
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        int subTaskId = this.newId();
        subtask.setId(subTaskId);
        subtasks.put(subTaskId, subtask);
        int epicIdOfSubTask = subtask.getEpicId();
        Epic epic = epics.get(epicIdOfSubTask);
        if (epic != null) {
            epic.addSubtaskId(subtask);
            this.setEpicStatus(epics.get(subtask.getEpicId()));
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
                    epic.getSubtasksId().clear();
                    setEpicStatus(epic);
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
        Epic newEpic = new Epic(epic.getName(), epic.getDescription(), currentEpicStatus
                , epic.getSubtasksId());
        if (epics.containsKey(idUpdatedEpic)) {
            epics.put(idUpdatedEpic, newEpic);
            setEpicStatus(newEpic);
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
            setEpicStatus(epic);
        }
    }

    public void deleteTaskById(int idNumber) {
        tasks.remove(idNumber);
    }

    public void deleteEpicById(int idNumber) {
        Epic epic = epics.get(idNumber);
        if (epic != null) {
            for (int sub : epic.getSubtasksId()) {
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
            epic.getSubtasksId().remove(Integer.valueOf(idNumber));
            setEpicStatus(epic);
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

    private void setEpicStatus(Epic epic) {
        Status oldTaskStatus = epic.getStatus();
        ArrayList<Subtask> subTasksUpd = new ArrayList<>();
        for (int i = 0; i < epic.getSubtasksId().size(); i++) {
            subTasksUpd.add(subtasks.get(epic.getSubtasksId().get(i)));
        }

        int counterDone = 0;
        int counterNew = 0;
        for (Subtask subtask : subTasksUpd) {
            switch (subtask.getStatus()) {
                case NEW:
                    counterNew++;
                    break;
                case IN_PROGRESS:
                    break;
                case DONE:
                    counterDone++;
                    break;
            }
        }
    }
}
