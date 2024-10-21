package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static manager.adapters.LocalDateTimeAdapter.FORMATTER;

public class Epic extends Task {

    private List<Integer> subtasksId = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Epic epic = (Epic) object;
        return Objects.equals(subtasksId, epic.subtasksId) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksId, endTime);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasksId=" + subtasksId +
                ", endTime=" + endTime +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + (duration != null ? duration.toMinutes() : 0) +
                ", startTime=" + ((startTime == null) ? "null" : startTime.format(FORMATTER)) +
                ", endTime=" + ((getEndTime() == null) ? "null" : getEndTime().format(FORMATTER)) +
                "} " + super.toString();
    }
}
