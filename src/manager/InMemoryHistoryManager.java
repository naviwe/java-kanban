package manager;

import task.Task;

import java.util.ArrayList;


public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> tasksHistory = new ArrayList<>();
    private static final int MAX_LIST_SIZE = 10; // Количество записей в списке истории

    @Override
    public void add(Task task) {
        tasksHistory.add(task);
        if (tasksHistory.size() > MAX_LIST_SIZE) {
            tasksHistory.remove(0);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(tasksHistory);
    }
}