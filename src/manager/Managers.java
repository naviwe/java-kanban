package manager;

public class Managers {

    public static HistoryManager getDefault() {
        HistoryManager historyManager = getDefaultHistory();
        return new InMemoryHistoryManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}