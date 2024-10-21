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
        if (isIntersection(task)) {
            return null;
        }
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
        if (isIntersection(subtask)) {
            return null;
        }
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(newId());
            Epic epic = epics.get(subtask.getEpicId());
            addSubtaskInternal(subtask);
            changeEpicStatus(epic);
            if (subtask.getDuration() != null && subtask.getStartTime() != null) {
                calculateStartTimeForEpic(subtask.getEpicId());
                calculateDurationTimeForEpic(subtask.getEpicId());
                calculateEndTimeForEpic(subtask.getEpicId());
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
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Integer epic : epics.keySet()) {
            historyManager.remove(epic);
        }
        for (Subtask sub : subtasks.values()) {
            historyManager.remove(sub.getId());
            prioritizedTasks.remove(sub);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtask() {
        for (Subtask sub : subtasks.values()) {
            historyManager.remove(sub.getId());
            prioritizedTasks.remove(sub);
            Epic epic = epics.get(sub.getEpicId());
            if (epic != null) {
                epic.removeSubtasksId();
                changeEpicStatus(epic);
                calculateStartTimeForEpic(sub.getEpicId());
                calculateDurationTimeForEpic(sub.getEpicId());
                calculateEndTimeForEpic(sub.getEpicId());
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
    public List<Subtask> getEpicSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subtaskIds = epic.getSubtasksId();

        return subtaskIds.stream()
                .map(id -> {
                    return getSubtaskByIdNumber(id);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @Override
    public void updateTask(Task task) {
        int idUpdatedTask = task.getId();
        Task oldTask = tasks.get(idUpdatedTask);

        if (oldTask != null) {
            prioritizedTasks.remove(oldTask);
            tasks.remove(idUpdatedTask);

            if (isIntersection(task)) {
                tasks.put(idUpdatedTask, oldTask);
                prioritizedTasks.add(oldTask);
                return;
            }

            tasks.put(idUpdatedTask, task);
            prioritizedTasks.add(task);
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
            if (currentSubtaskId.getEpicId() == subtask.getEpicId()) {
                prioritizedTasks.remove(currentSubtaskId);
                subtasks.remove(subtask.getId());

                if (isIntersection(subtask)) {
                    subtasks.put(subtask.getId(), currentSubtaskId);
                    prioritizedTasks.add(currentSubtaskId);
                    return;
                }

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
        prioritizedTasks.remove(tasks.get(idNumber));
    }

    @Override
    public void deleteEpicById(int idNumber) {
        Epic epic = epics.get(idNumber);
        if (epic != null) {
            for (int sub : epic.getSubtasksId()) {
                subtasks.remove(sub);
                historyManager.remove(sub);
                prioritizedTasks.remove(subtasks.get(sub));
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
        prioritizedTasks.remove(sub);
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

        List<Subtask> epicSubtasks = getEpicSubtasksByEpicId(epic.getId());
        if (epicSubtasks.isEmpty()) {
            epic.setStartTime(null);
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

        epic.setStartTime(minStartTime);
        calculateDurationTimeForEpic(epicId);
    }


    @Override
    public void calculateDurationTimeForEpic(Integer epicId) {
        Duration duration = Duration.ZERO;

        Epic epic = epics.get(epicId);

        if (epic == null) {
            return;
        }

        List<Subtask> epicSubtasks = getEpicSubtasksByEpicId(epic.getId());
        if (epicSubtasks.isEmpty()) {
            epic.setDuration(null);
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
        if (duration.isZero()) {
            epic.setDuration(null);
        } else {
            epic.setDuration(duration);
        }
    }


    @Override
    public void calculateEndTimeForEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        List<LocalDateTime> endTimes = new ArrayList<>();

        List<Subtask> epicSubtasks = getEpicSubtasksByEpicId(epic.getId());
        if (epicSubtasks.isEmpty()) {
            epic.setEndTime(null);
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
        } else {
            epic.setEndTime(null);
        }
    }

    protected boolean isIntersection(Task task) {
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