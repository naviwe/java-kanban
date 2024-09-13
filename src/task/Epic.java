package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksId = new ArrayList<>();


    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic() {

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

    public String getListOfIdOfSubtasks() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < subtasksId.size(); i++) {
            sb.append(subtasksId.get(i));
            if (i != subtasksId.size() - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s\n", (getId()),
                getType(), getName(),
                getStatus(),
                getDescription());
    }
}
