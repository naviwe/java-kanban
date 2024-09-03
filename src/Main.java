
import manager.*;
import task.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        TaskManager taskManager = Managers.getDefault();
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        HistoryManager historyManager = Managers.getDefaultHistory();

        //Задача id 1
        Task takeExams = new Task("Права","Сдать Экзамены", Status.NEW);
        inMemoryTaskManager.addTask(takeExams);

        //Задача id 2
        Task pickUpTheChild = new Task("Школа","Заехать в школу", Status.NEW);
        inMemoryTaskManager.addTask(pickUpTheChild);
        System.out.println("Задача под id "+ pickUpTheChild.getId()+" : "+ inMemoryTaskManager.getTaskByIdNumber(2));
        //Эпик id 3
        Epic buyProducts = new Epic("Покупки","Купить продукты");
        inMemoryTaskManager.addEpic(buyProducts);

        //Подзадача id 4, эпик id 3
        Subtask milk = new Subtask("Молоко", "Молоко", Status.NEW, 3);
        inMemoryTaskManager.addSubtask(milk);

        //Подзадача id 5, эпик id 3
        Subtask bread = new Subtask("Хлеб", "Хлеб", Status.NEW, 3);
        inMemoryTaskManager.addSubtask(bread);


        inMemoryTaskManager.getTaskByIdNumber(1);
        inMemoryTaskManager.getTaskByIdNumber(2);
        inMemoryTaskManager.getTaskByIdNumber(1);
        inMemoryTaskManager.getEpicByIdNumber(3);
        inMemoryTaskManager.getSubtaskByIdNumber(8);
        inMemoryTaskManager.getEpicByIdNumber(7);
        inMemoryTaskManager.getSubtaskByIdNumber(9);
        inMemoryTaskManager.getSubtaskByIdNumber(5);
        inMemoryTaskManager.getSubtaskByIdNumber(4);
        inMemoryTaskManager.getSubtaskByIdNumber(6);
        inMemoryTaskManager.getSubtaskByIdNumber(8);
        inMemoryTaskManager.getSubtaskByIdNumber(9);



    }
}