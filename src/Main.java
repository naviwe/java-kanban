
import manager.*;
import task.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();

        Task firstTask = new Task("Выкинуть мусор", "Через 20 минут", Status.NEW);
        manager.addTask(firstTask);

        Task secondTask = new Task("Убраться дома", "Помыть полы и посуду", Status.NEW);
        manager.addTask(secondTask);

        Epic firstEpic = new Epic("Убраться дома", "Потому что придут гости");
        manager.addEpic(firstEpic);

        Subtask firstSubtask = new Subtask(
                "Убрать полы", "Пропылесосить и помыть", Status.DONE, 1);
        manager.addSubtask(firstSubtask);

        Subtask secondSubtask = new Subtask(
                "Помыть посуду", "Тарелки и стаканы", Status.DONE, 2);
        manager.addSubtask(secondSubtask);
        System.out.println("Получение списка всех задач");
        System.out.println(manager.getTasksList());
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubtaskList());

        System.out.println("Получение по идентификатору");
        System.out.println(manager.getTaskByIdNumber(1));
        System.out.println(manager.getEpicByIdNumber(3));
        System.out.println(manager.getSubtaskByIdNumber(5));

        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubtask();

        System.out.println("Удаление всех задач");
        System.out.println(manager.getTasksList());
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubtaskList());

        System.out.println("Создание");
        Task newFirstTask = manager.createTask(firstTask);
        manager.addTask(newFirstTask);
        Task newSecondTask = manager.createTask(secondTask);
        manager.addTask(newSecondTask);

        Epic newEpic = manager.createEpic(firstEpic);
        manager.addEpic(firstEpic);
        Subtask newSubtask = manager.createSubtask(firstSubtask);
        manager.addSubtask(firstSubtask);

        System.out.println(newFirstTask);
        System.out.println(newSecondTask);
        System.out.println(newEpic);
        System.out.println(newSubtask);

        System.out.println("Обновление");
        manager.updateTask(newFirstTask);
        manager.updateEpic(newEpic);
        manager.updateSubtask(newSubtask);
        System.out.println(manager.getTasksList());
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubtaskList());

        System.out.println("Получение списка всех подзадач определённого эпика");
        System.out.println(manager.subtaskList(2));

        System.out.println("Удаление по идентификатору");

        manager.deleteTaskById(5);
        manager.deleteTaskById(4);
        manager.deleteEpicById(6);


        System.out.println(manager.getTasksList());
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubtaskList());

    }
}