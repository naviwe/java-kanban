package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    ArrayList<Task> getTasksList();

    ArrayList<Epic> getEpicsList();

    ArrayList<Subtask> getSubtaskList();

    void deleteTasks();

    void deleteEpics();

    void deleteSubtask();

    Task getTaskByIdNumber(int idNumber);

    Epic getEpicByIdNumber(int idNumber);

    Subtask getSubtaskByIdNumber(int idNumber);

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int idNumber);

    void deleteEpicById(int idNumber);

    void deleteSubtaskById(int idNumber);

    ArrayList<Subtask> subtaskList(int idNumber);

    void changeEpicStatus(Epic epic);

    ArrayList<Task> getHistory();
}
