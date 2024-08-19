package manager;

import task.*;

import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idNumber = 0;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(){
        this.historyManager = Managers.getDefaultHistory();
    }
    private int newId() {
        return ++idNumber;
    }

    @Override
    public void addTask(Task task) {
        task.setId(newId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(newId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(newId());
            Epic epic = epics.get(subtask.getEpicId());
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtaskId(subtask);
            changeEpicStatus(epic);
        }
    }

    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
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

    @Override
    public Task getTaskByIdNumber(int idNumber) {
        historyManager.add(tasks.get(idNumber));
        return tasks.get(idNumber);
    }

    @Override
    public Epic getEpicByIdNumber(int idNumber) {
        historyManager.add(epics.get(idNumber));
        return epics.get(idNumber);
    }

    @Override
    public Subtask getSubtaskByIdNumber(int idNumber) {
        historyManager.add(subtasks.get(idNumber));
        return subtasks.get(idNumber);
    }

    @Override
    public Task createTask(Task task) {
        return new Task(task.getName(), task.getDescription(), task.getStatus());
    }

    @Override
    public Epic createEpic(Epic epic) {
        return new Epic(epic.getName(), epic.getDescription());
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        return new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
    }

    @Override
    public void updateTask(Task task) {
        int idUpdatedTask = task.getId();
        if (tasks.containsKey(idUpdatedTask)) {
            tasks.put(idUpdatedTask, task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int idUpdatedEpic = epic.getId();
        if (epics.containsKey(idUpdatedEpic)) {
            epics.get(idUpdatedEpic).setName(epic.getName());
            epics.get(idUpdatedEpic).setDescription(epic.getDescription());
        }
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask currentSubtaskId = subtasks.get(subtask.getId());
            if (currentSubtaskId.getEpicId() == (subtask.getEpicId())) {
                subtasks.put(subtask.getId(), subtask);
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    changeEpicStatus(epic);
                }
            }
        }
    }

    @Override
    public void deleteTaskById(int idNumber) {
        tasks.remove(idNumber);
    }

    @Override
    public void deleteEpicById(int idNumber) {
        Epic epic = epics.get(idNumber);
        if (epic != null) {
            for (int sub : epic.getSubtasksId()) {
                subtasks.remove(sub);
            }
        }
        epics.remove(idNumber);
    }

    @Override
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

    @Override
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

    @Override
    public void changeEpicStatus(Epic epic) {
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


    @Override
    public HistoryManager getHistory() {
        return historyManager;
    }
}