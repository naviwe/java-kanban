package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{
    private List<Integer> subtask = new ArrayList<>();


    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(int id, String name, String description, Status status, List<Integer> subtask) {
        super(id, name, description, status);
        this.subtask = subtask;
    }

    public List<Integer> getSubtask() {
        return subtask;
    }

    public void setSubtask(ArrayList<Integer> subtask) {
        this.subtask = subtask;
    }
    public void addSubtaskId(Subtask sub) {
        subtask.add(sub.getId());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                "} ";
    }
}
