package manager;

public class Managers {

    public static HistoryManager getDefaultHistory() {
        HistoryManager historyManager = getDefaultHistory();
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTask() {
        return new InMemoryTaskManager();
    }
}