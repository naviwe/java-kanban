package manager;

import task.Epic;
import task.Subtask;
import task.Task;

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

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int idNumber);

    void deleteEpicById(int idNumber);

    void deleteSubtaskById(int idNumber);

    List<Subtask> subtaskList(int idNumber);

    void changeEpicStatus(Epic epic);

    List<Task> getHistory();
}
