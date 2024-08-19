package manager;

import task.Task;


import java.util.ArrayList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {


    private final List<Task> tasksHistory;
    private static final int MAX_LIST_SIZE = 10; // Количество записей в списке истории

    public InMemoryHistoryManager() {
        tasksHistory = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        tasksHistory.add(task);
        if (tasksHistory.size() > MAX_LIST_SIZE) {
            tasksHistory.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }
}