package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksId = new ArrayList<>();


    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }


    public List<Integer> getSubtasksId() {
        return new ArrayList<>(subtasksId);
    }

    public void setSubtasksId(List<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    public void removeSubtaskId(int subtaskId) {
        subtasksId.remove(Integer.valueOf(subtaskId));
    }

    public void addSubtaskId(Subtask sub) {
        subtasksId.add(sub.getId());
    }

    public void removeSubtasksId() {
        subtasksId.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                "} ";
    }
}