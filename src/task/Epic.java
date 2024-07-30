package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{
    private List<Integer> subtasksId = new ArrayList<>();


    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description, Status status, List<Integer> subtasksId) {
        super(name, description, status);
        this.subtasksId = subtasksId;
    }


    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void removeSubtaskId(int subtaskId) {
        subtasksId.remove(subtaskId);
    }
    public void addSubtaskId(Subtask sub) {
        subtasksId.add(sub.getId());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", subtasksId=" + subtasksId +
                "} ";
    }
}
