package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.util.List;

public interface TaskManager {
    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);

    List<Task> getTasksList();

    List<Epic> getEpicsList();

    List<Subtask> getSubtaskList();

    void deleteTasks();

    void deleteEpics();

    void deleteSubtask();

    Task getTaskByIdNumber(int idNumber);

    Epic getEpicByIdNumber(int idNumber);

    Subtask getSubtaskByIdNumber(int idNumber);

    List<Subtask> getEpicSubtasksByEpicId(int epicId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int idNumber);

    void deleteEpicById(int idNumber);

    void deleteSubtaskById(int idNumber);

    void changeEpicStatus(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    void calculateStartTimeForEpic(Integer epicId);

    void calculateDurationTimeForEpic(Integer epicId);

    void calculateEndTimeForEpic(Integer epicId);
}
