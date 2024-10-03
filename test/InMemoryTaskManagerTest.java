import manager.InMemoryTaskManager;
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

class InMemoryTaskManagerTest extends InMemoryTaskManager {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void whenGetPrioritizedTasks_thenReturnTreeSetFillOfTasks() {
        Task task1 = new Task("Task1", "DescriptionTask1", Status.NEW);
        Task task2 = new Task("Task2", "DescriptionTask2", Status.NEW);
        Epic epic1 = new Epic("Epic1", "DescriptionEpic1");
        task1.setStartTime(LocalDateTime.of(2023, Month.OCTOBER, 4, 21, 59));
        task2.setStartTime(LocalDateTime.of(2023, Month.OCTOBER, 4, 22, 30));
        epic1.setStartTime(LocalDateTime.of(2023, Month.OCTOBER, 3, 20, 10));
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
        taskManager.addTask(task1);
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        Subtask subtask = new Subtask("Subtask1", "DescriptionForEpic1", Status.NEW, epic1.getId());
        taskManager.addSubtask(subtask);
        assertEquals(3, taskManager.getPrioritizedTasks().size());
        assertEquals(task1, taskManager.getPrioritizedTasks().get(0));
    }

    @Test
    public void givenStartTimeAndDuration_whenAddSubtask_thenCalculateTimeFieldsOfEpic() {
        Epic epic1 = new Epic("Epic1", "DescriptionEpic1");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "DescriptionSubtask1", Status.NEW,
                LocalDateTime.of(2023, Month.OCTOBER, 5, 10, 1), Duration.ofMinutes(59), epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "DescriptionSubtask2", Status.NEW,
                LocalDateTime.of(2023, Month.OCTOBER, 5, 11, 0), Duration.ofMinutes(30), epic1.getId());

        taskManager.addSubtask(subtask1);
        assertEquals(LocalDateTime.of(2023, Month.OCTOBER, 5, 10, 1), epic1.getStartTime());
        assertEquals(Duration.ofMinutes(59), epic1.getDuration());

        taskManager.addSubtask(subtask2);
        assertEquals(LocalDateTime.of(2023, Month.OCTOBER, 5, 10, 1), epic1.getStartTime());
        assertEquals(Duration.ofMinutes(89), epic1.getDuration());
        assertFalse(isIntersectionTest(subtask1));

        Task task1 = new Task("Task1", "DescriptionTask1", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2023, Month.OCTOBER, 5, 10, 20));
        task1.setDuration(Duration.ofMinutes(10));
        taskManager.addTask(task1);
        assertFalse(isIntersectionTest(task1));

        taskManager.calculateEndTimeForEpic(epic1.getId());
        assertEquals(LocalDateTime.of(2023, Month.OCTOBER, 5, 11, 30), epic1.getEndTime());
    }

    public boolean isIntersectionTest(Task task) {
        return isIntersection(task);
    }
}