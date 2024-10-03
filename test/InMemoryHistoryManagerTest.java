package test;

import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryHistoryManagerTest {

    private InMemoryTaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testShouldReturnEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void checkUpdateIdWhenCreateNewTask() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.addTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        taskManager.addTask(task2);

        assertNotEquals(task1.getId(), task2.getId());

        assertEquals(task1.getId(), 1);
        assertEquals(task2.getId(), 2);

        Epic epic = new Epic("Test epic", "Test description");
        taskManager.addEpic(epic);
        assertEquals(epic.getId(), 3);

        Subtask subtask = new Subtask("Test subtask", "Test description", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask);
        assertEquals(subtask.getId(), 4);

    }

    @Test
    void testTasksEqualityById() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 1", "Description 1", Status.NEW);
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    void testSubtaskEqualityById() {
        Epic epic = new Epic("Epic 1", "Epic description");
        epic.setId(1);
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description 1", Status.NEW, epic.getId());
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask description 2", Status.NEW, epic.getId());
        subtask2.setId(1);

        assertEquals(subtask1.getId(), subtask2.getId());
    }


    @Test
    void testSubtaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Epic 1", "Epic description");
        epic.setId(1);
        Subtask subtask = new Subtask("Subtask", "Description", Status.NEW, epic.getId());
        subtask.setId(1);

        assertNotEquals(taskManager.addEpic(epic), null);
    }

    @Test
    void testManagerAlwaysReturnsInitializedManagers() {
        assertNotNull(taskManager.getTasksList());
        assertNotNull(taskManager.getEpicsList());
        assertNotNull(taskManager.getSubtaskList());
    }

    @Test
    void testTaskManagerAddsAndFindsTasksById() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.addTask(task);

        assertNotNull(taskManager.getTaskByIdNumber(task.getId()));
    }

    @Test
    void testTaskManagerAddsAndFindsEpicsById() {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.addEpic(epic);

        assertNotNull(taskManager.getEpicByIdNumber(epic.getId()));
    }

    @Test
    void testTasksWithGeneratedAndSpecifiedIdDoNotConflict() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);
        taskManager.addTask(task1);

        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        taskManager.addTask(task2);

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void testTaskImmutabilityWhenAddedToManager() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTaskByIdNumber(task.getId());
        assertEquals(task, retrievedTask);
        assertEquals(task.getName(), retrievedTask.getName());
    }

    @Test
    void testHistoryManagerStoresPreviousTaskVersions() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.addTask(task);
        historyManager.add(task);

        task.setName("Updated Task 1");
        taskManager.updateTask(task);

        Task actualTask = taskManager.getTaskByIdNumber(task.getId());
        Task fromHistory = historyManager.getHistory().getFirst();
        assertEquals(actualTask.getId(), fromHistory.getId());
        assertEquals("Description 1", fromHistory.getDescription());
    }
}