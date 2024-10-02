
import manager.*;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryLinkedListTest {
    private InMemoryHistoryManager historyManager;

    @Test
    void addTestHistoryIsEmpty() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Задача", "Функция создания задачи", Status.NEW);
        Epic epic = new Epic("Эпик-задача", "Функция создания");
        Subtask subtask = new Subtask("Подзадача 1", "Подзадача эпика", Status.NEW, epic.getId());
        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        assertNotNull(task, "Задача не создана");
        assertNotNull(epic, "Задача не создана");
        assertNotNull(subtask, "Задача не создана");

        assertEquals(List.of(), historyManager.getHistory(), "Задача отразились в истории");


    }

    @Test
    void getHistoryTestHistoryIsEmpty() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertEquals(List.of(), historyManager.getHistory(), "Задача отразились в истории");
    }

    @Test
    void removeTestHistoryIsEmpty() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Задача", "Функция создания задачи", Status.NEW);
        Epic epic = new Epic("Эпик-задача", "Функция создания");
        Subtask subtask = new Subtask("Подзадача 1", "Подзадача эпика", Status.NEW, epic.getId());
        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        assertNotNull(task, "Задача не создана");
        assertNotNull(epic, "Задача не создана");
        assertNotNull(subtask, "Задача не создана");

        historyManager.remove(task.getId());
        historyManager.remove(epic.getId());
        historyManager.remove(subtask.getId());

        assertEquals(List.of(), historyManager.getHistory());

    }

    //    Дублирование.
    @Test
    void addAndGetTestDuplication() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Задача", "Функция создания задачи", Status.NEW);
        Epic epic = new Epic("Эпик-задача", "Функция создания");
        Subtask subtask = new Subtask("Подзадача 1", "Подзадача эпика", Status.NEW, epic.getId());
        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        assertNotNull(task, "Задача не создана");
        assertNotNull(epic, "Задача не создана");
        assertNotNull(subtask, "Задача не создана");

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(task);
        String expectedString = "[" + epic.toString() + ", " + subtask.toString() + ", " + task.toString() + "]";

        assertEquals(expectedString, historyManager.getHistory().toString(), "Задача не отразились в истории");
    }

    @Test
    void removeTestDuplication() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Задача", "Функция создания задачи", Status.NEW);
        Epic epic = new Epic("Эпик-задача", "Функция создания");
        Subtask subtask = new Subtask("Подзадача 1", "Подзадача эпика", Status.NEW, epic.getId());
        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        assertNotNull(task, "Задача не создана");
        assertNotNull(epic, "Задача не создана");
        assertNotNull(subtask, "Задача не создана");

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(task);
        String expectedString = "[" + epic.toString() + ", " + subtask.toString() + ", " + task.toString() + "]";


        historyManager.remove(task.getId());
        historyManager.remove(epic.getId());
        historyManager.remove(subtask.getId());

        assertEquals(List.of(), historyManager.getHistory());
    }

    //    Удаление из истории: начало, середина, конец.
    @Test
    void removeTestRemoveBeginning() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Задача", "Функция создания задачи", Status.NEW);
        Epic epic = new Epic("Эпик-задача", "Функция создания");
        Subtask subtask = new Subtask("Подзадача 1", "Подзадача эпика", Status.NEW, epic.getId());
        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        assertNotNull(task, "Задача не создана");
        assertNotNull(epic, "Задача не создана");
        assertNotNull(subtask, "Задача не создана");

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.remove(task.getId());
        String expectedString = "[" + epic.toString() + ", " + subtask.toString() + "]";

        assertEquals(expectedString, historyManager.getHistory().toString(), "Задача не отразились в истории");
    }

    @Test
    void removeTestRemoveMiddle() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Задача", "Функция создания задачи", Status.NEW);
        Epic epic = new Epic("Эпик-задача", "Функция создания");
        Subtask subtask = new Subtask("Подзадача 1", "Подзадача эпика", Status.NEW, epic.getId());
        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        assertNotNull(task, "Задача не создана");
        assertNotNull(epic, "Задача не создана");
        assertNotNull(subtask, "Задача не создана");

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.remove(epic.getId());
        String expectedString = "[" + task.toString() + ", " + subtask.toString() + "]";

        assertEquals(expectedString, historyManager.getHistory().toString(), "Задача не отразились в истории");
    }

    @Test
    void removeTestRemoveEnd() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Задача", "Функция создания задачи", Status.NEW);
        Epic epic = new Epic("Эпик-задача", "Функция создания");
        Subtask subtask = new Subtask("Подзадача 1", "Подзадача эпика", Status.NEW, epic.getId());
        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubtask(subtask);
        assertNotNull(task, "Задача не создана");
        assertNotNull(epic, "Задача не создана");
        assertNotNull(subtask, "Задача не создана");

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.remove(subtask.getId());
        String expectedString = "[" + task.toString() + ", " + epic.toString() + "]";

        assertEquals(expectedString, historyManager.getHistory().toString(), "Задача не отразились в истории");
    }
}
