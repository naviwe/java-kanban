
import manager.*;
import task.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        Task takeExams = new Task("Права", "Сдать Экзамены", Status.NEW);
        inMemoryTaskManager.addTask(takeExams);

        Task pickUpTheChild = new Task("Школа", "Заехать в школу", Status.NEW);
        inMemoryTaskManager.addTask(pickUpTheChild);
        System.out.println("Задача под id " + pickUpTheChild.getId() + " : " + inMemoryTaskManager.getTaskByIdNumber(2));

        Epic buyProducts = new Epic("Покупки", "Купить продукты");
        inMemoryTaskManager.addEpic(buyProducts);

        Subtask milk = new Subtask("Молоко", "Молоко", Status.NEW, 3);
        inMemoryTaskManager.addSubtask(milk);

        Subtask bread = new Subtask("Хлеб", "Хлеб", Status.NEW, 3);
        inMemoryTaskManager.addSubtask(bread);
    }
}