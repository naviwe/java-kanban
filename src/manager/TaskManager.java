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
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(newId());
            Epic epic = epics.get(subtask.getEpicId());
            subtasks.put(subtask.getId(), subtask);
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
                    epic.removeSubtasksId();
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
        return new Epic(epic.getName(), epic.getDescription());
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
        if (epics.containsKey(idUpdatedEpic)) {
            epics.get(idUpdatedEpic).setName(epic.getName());
            epics.get(idUpdatedEpic).setDescription(epic.getDescription());
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask currentSubtaskId = subtasks.get(subtask.getId());
            if (currentSubtaskId.getEpicId() == (subtask.getEpicId())) {
                subtasks.put(subtask.getId(), subtask);
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null){
                    changeEpicStatus(epic);
                }
            }
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
            epic.removeSubtaskId(idNumber);
            changeEpicStatus(epic);
        }
        subtasks.remove(idNumber);
    }

    public ArrayList<Subtask> subtaskList(int idNumber) {
        ArrayList<Subtask> listSubtasks = new ArrayList<>();
        Epic epic = epics.get(idNumber);
        if (epic != null) {
            for (Integer sub : epic.getSubtasksId()) {
                Subtask subtask = subtasks.get(sub);
                listSubtasks.add(subtask);
            }
        }
        return listSubtasks;
    }

    private void changeEpicStatus(Epic epic) {
        ArrayList<Subtask> subTasksUpd = new ArrayList<>();
        for (Integer subId : epic.getSubtasksId()) {
            subTasksUpd.add(subtasks.get(subId));
        }

        int counterDone = 0;
        int counterNew = 0;
        for (Subtask subtask : subTasksUpd) {
            switch (subtask.getStatus()) {
                case NEW:
                    counterNew++;
                    break;
                case IN_PROGRESS:
                    return;
                case DONE:
                    counterDone++;
                    break;
            }
        }

        if ((subTasksUpd.size() == 0) || (counterNew == subTasksUpd.size())) {
            epic.setStatus(Status.NEW);
        } else if (counterDone == subTasksUpd.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}