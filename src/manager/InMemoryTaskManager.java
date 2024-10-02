package manager;

import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected Map<Integer, Task> tasks;
    protected Map<Integer, Epic> epics;
    protected Map<Integer, Subtask> subtasks;
    protected int idNumber = 0;
    protected final HistoryManager historyManager;
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.nullsLast((o1, o2) -> {
        if (o1.getStartTime() != null && o2.getStartTime() != null) {
            return o1.getStartTime().compareTo(o2.getStartTime());
        } else if (o1 == o2) {
            return 0;
        } else {
            return 1;
        }
    }));

    public InMemoryTaskManager() {
        historyManager = new InMemoryHistoryManager();
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int newId() {
        return ++idNumber;
    }

    protected Task addTaskInternal(Task task) {
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    protected Epic addEpicInternal(Epic epic) {
        epics.put(epic.getId(), epic);
        prioritizedTasks.add(epic);
        return epic;
    }

    protected Subtask addSubtaskInternal(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask);
        }
        return subtask;
    }


    @Override
    public Task addTask(Task task) {
        task.setId(newId());
        return addTaskInternal(task);
    }


    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(newId());
        return addEpicInternal(epic);
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(newId());
            Epic epic = epics.get(subtask.getEpicId());
            addSubtaskInternal(subtask);
            changeEpicStatus(epic);
            if (subtask.getDuration() != null && subtask.getStartTime() != null) {
                calculateStartTimeForEpic(subtask.getEpicId());
                calculateDurationTimeForEpic(subtask.getEpicId());
            }
        }
        return subtask;
    }


    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteTasks() {
        for (Integer task : tasks.keySet()) {
            historyManager.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Integer epic : epics.keySet()) {
            historyManager.remove(epic);
        }
        for (Integer sub : subtasks.keySet()) {
            historyManager.remove(sub);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtask() {
        for (Integer sub : subtasks.keySet()) {
            historyManager.remove(sub);
        }
        for (Integer sub : subtasks.keySet()) {
            Subtask subtask = subtasks.get(sub);
            if (subtask != null) {
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.removeSubtasksId();
                    changeEpicStatus(epic);
                    calculateStartTimeForEpic(subtask.getEpicId());
                    calculateDurationTimeForEpic(subtask.getEpicId());
                    calculateEndTimeForEpic(subtask.getEpicId());
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
    public List<Subtask> getEpicSubtasksByEpicId(Epic epic) {
        List<Integer> subtaskIds = epic.getSubtasksId();

        return subtaskIds.stream()
                .map(this::getSubtaskByIdNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @Override
    public void updateTask(Task task) {
        int idUpdatedTask = task.getId();
        if (tasks.containsKey(idUpdatedTask)) {
            prioritizedTasks.remove(task);
            tasks.put(idUpdatedTask, task);
            prioritizedTasks.add(tasks.get(idUpdatedTask));
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int idUpdatedEpic = epic.getId();
        if (epics.containsKey(idUpdatedEpic)) {
            prioritizedTasks.remove(epic);
            epics.get(idUpdatedEpic).setName(epic.getName());
            epics.get(idUpdatedEpic).setDescription(epic.getDescription());
            prioritizedTasks.add(epics.get(idUpdatedEpic));
        }
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask currentSubtaskId = subtasks.get(subtask.getId());
            if (currentSubtaskId.getEpicId() == (subtask.getEpicId())) {
                prioritizedTasks.remove(subtask);
                subtasks.put(subtask.getId(), subtask);
                prioritizedTasks.add(subtask);
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    changeEpicStatus(epic);
                    calculateStartTimeForEpic(subtask.getEpicId());
                    calculateDurationTimeForEpic(subtask.getEpicId());
                    calculateEndTimeForEpic(subtask.getEpicId());
                }
            }
        }
    }

    @Override
    public void deleteTaskById(int idNumber) {
        tasks.remove(idNumber);
        historyManager.remove(idNumber);
    }

    @Override
    public void deleteEpicById(int idNumber) {
        Epic epic = epics.get(idNumber);
        if (epic != null) {
            for (int sub : epic.getSubtasksId()) {
                subtasks.remove(sub);
                historyManager.remove(sub);
            }
        }
        epics.remove(idNumber);
        historyManager.remove(idNumber);
    }

    @Override
    public void deleteSubtaskById(int idNumber) {
        Subtask sub = subtasks.get(idNumber);
        int epicId = sub.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.removeSubtaskId(idNumber);
            changeEpicStatus(epic);
            calculateStartTimeForEpic(epicId);
            calculateDurationTimeForEpic(epicId);
            calculateEndTimeForEpic(epicId);
        }
        subtasks.remove(idNumber);
        historyManager.remove(idNumber);
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

        if ((subTasksUpd.isEmpty()) || (counterNew == subTasksUpd.size())) {
            epic.setStatus(Status.NEW);
        } else if (counterDone == subTasksUpd.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void calculateStartTimeForEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        List<Subtask> epicSubtasks = getEpicSubtasksByEpicId(epic);
        if (epicSubtasks.isEmpty()) {
            return;
        }

        LocalDateTime minStartTime = null;

        for (Subtask subtask : epicSubtasks) {
            if (subtask != null) {
                LocalDateTime startTime = subtask.getStartTime();
                if (startTime != null && (minStartTime == null || startTime.isBefore(minStartTime))) {
                    minStartTime = startTime;
                }
            }
        }

        if (minStartTime != null) {
            epic.setStartTime(minStartTime);
        }

        calculateDurationTimeForEpic(epicId);
    }


    @Override
    public void calculateDurationTimeForEpic(Integer epicId) {
        Duration duration = Duration.ZERO;
        if (epics.isEmpty()) {
            return;
        }

        Epic epic = epics.get(epicId);

        if (epic == null) {
            return;
        }

        List<Subtask> epicSubtasks = getEpicSubtasksByEpicId(epic);
        if (epicSubtasks.isEmpty()) {
            return;
        }

        for (Subtask sub : epicSubtasks) {
            if (sub != null) {
                if (sub.getDuration() == null) {
                    epic.setDuration(duration);
                    return;
                }
                duration = duration.plus(sub.getDuration());
            }
        }

        epic.setDuration(duration);
    }


    @Override
    public void calculateEndTimeForEpic(Integer epicId) {
        if (epics.isEmpty()) {
            return;
        }

        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        List<LocalDateTime> endTimes = new ArrayList<>();

        List<Subtask> epicSubtasks = getEpicSubtasksByEpicId(epic);
        if (epicSubtasks.isEmpty()) {
            return;
        }
        for (Subtask subtask : epicSubtasks) {
            if (subtask.getDuration() != null) {
                endTimes.add(subtask.getEndTime());
            }
        }

        if (!endTimes.isEmpty()) {
            LocalDateTime maxEndTime = Collections.max(endTimes);
            epic.setEndTime(maxEndTime);
        }
    }

    @Override
    public boolean isIntersection(Task task) {
        if (task == null || task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }
        for (Task prioritized : getPrioritizedTasks()) {
            if (prioritized.getStartTime() == null || prioritized.getEndTime() == null) {
                continue;
            }
            if (task.getStartTime().isBefore(prioritized.getEndTime()) && task.getEndTime().isAfter(prioritized.getStartTime())) {
                return true;
            }
        }
        return false;
    }


}