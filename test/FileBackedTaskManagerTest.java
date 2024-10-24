import manager.FileBackedTasksManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {

    @Test
    public void testFileSaveAndLoad() throws IOException, InterruptedException {
        File file = new File("history.csv");
        TaskManager taskManager = new FileBackedTasksManager(file);
        Task task1 = new Task("Задача 1", "Действие таска 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Действие таска 2", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Epic epic1 = new Epic("Эпик 1", "Действие эпика 1");
        taskManager.addEpic(epic1);
        Subtask subTask11 = new Subtask("Подзадача 1", "Действие подтаска 1", Status.DONE, epic1.getId());
        Subtask subTask12 = new Subtask("Подзадача 2", "Действие подтаска 2", Status.NEW, epic1.getId());
        Subtask subTask13 = new Subtask("Подзадача 3", "Действие подтаска 2", Status.NEW, epic1.getId());
        taskManager.addSubtask(subTask11);
        taskManager.addSubtask(subTask12);
        taskManager.addSubtask(subTask13);
        Epic epic2 = new Epic("Эпик 2", "Действие эпика 2");
        taskManager.addEpic(epic2);
        Subtask subTask21 = new Subtask("Подзадача 1", "Действие", Status.DONE, epic2.getId());
        taskManager.addSubtask(subTask21);
        List<Task> tasksBeforeLoad = taskManager.getTasksList();
        List<Epic> epicsBeforeLoad = taskManager.getEpicsList();
        List<Subtask> subTasksBeforeLoad = taskManager.getSubtaskList();
        ((FileBackedTasksManager) taskManager).loadFromFile(file);
        List<Task> tasksAfterLoad = taskManager.getTasksList();
        List<Epic> epicsAfterLoad = taskManager.getEpicsList();
        List<Subtask> subTasksAfterLoad = taskManager.getSubtaskList();
        for (int i = 0; i < tasksBeforeLoad.size(); i++) {
            assertEquals(tasksBeforeLoad.get(i), tasksAfterLoad.get(i));
        }
        for (int i = 0; i < epicsBeforeLoad.size(); i++) {
            assertEquals(epicsBeforeLoad.get(i), epicsAfterLoad.get(i));
        }
        for (int i = 0; i < subTasksBeforeLoad.size(); i++) {
            assertEquals(subTasksBeforeLoad.get(i), subTasksAfterLoad.get(i));
        }
    }

    @Test
    public void testEmptyFileSaveAndLoad() throws IOException, InterruptedException {
        File file = new File("history.csv");
        FileBackedTasksManager emptyManager = new FileBackedTasksManager(file);
        emptyManager.save();
        assertTrue(emptyManager.getTasksList().isEmpty());
        assertTrue(emptyManager.getEpicsList().isEmpty());
        assertTrue(emptyManager.getSubtaskList().isEmpty());
        emptyManager.loadFromFile(file);
        assertTrue(emptyManager.getTasksList().isEmpty());
        assertTrue(emptyManager.getEpicsList().isEmpty());
        assertTrue(emptyManager.getSubtaskList().isEmpty());
    }


}