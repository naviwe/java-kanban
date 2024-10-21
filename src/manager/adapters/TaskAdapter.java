package manager.adapters;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import task.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskAdapter extends TypeAdapter<Task> {

    @Override
    public void write(JsonWriter jsonWriter, Task task) throws IOException {
        if (task == null) {
            jsonWriter.nullValue();
            return;
        }

        jsonWriter.beginObject();
        jsonWriter.name("id").value(task.getId());
        jsonWriter.name("name").value(task.getName());
        jsonWriter.name("description").value(task.getDescription());
        jsonWriter.name("status").value(task.getStatus().name());
        jsonWriter.name("type").value(task.getType().name());

        if (task.getStartTime() != null) {
            jsonWriter.name("startTime").value(task.getStartTime().format(LocalDateTimeAdapter.FORMATTER));
        } else {
            jsonWriter.name("startTime").nullValue();
        }

        if (task.getDuration() != null) {
            jsonWriter.name("duration").value(task.getDuration().toMinutes());
        } else {
            jsonWriter.name("duration").nullValue();
        }

        if (task instanceof Subtask) {
            jsonWriter.name("epicId").value(((Subtask) task).getEpicId());
        }

        jsonWriter.endObject();
    }

    @Override
    public Task read(JsonReader jsonReader) throws IOException {
        JsonElement jsonElement = JsonParser.parseReader(jsonReader);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        int id = jsonObject.get("id").getAsInt();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        Status status = Status.valueOf(jsonObject.get("status").getAsString());
        String type = jsonObject.get("type").getAsString();

        LocalDateTime startTime = null;
        if (!jsonObject.get("startTime").isJsonNull()) {
            startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), LocalDateTimeAdapter.FORMATTER);
        }

        Duration duration = null;
        if (!jsonObject.get("duration").isJsonNull()) {
            duration = Duration.ofMinutes(jsonObject.get("duration").getAsLong());
        }

        Task task;
        switch (TaskType.valueOf(type)) {
            case TASK:
                task = new Task(name, description, status, startTime, duration);
                break;
            case EPIC:
                task = new Epic(name, description, status, startTime, duration);
                break;
            case SUBTASK:
                int epicId = jsonObject.get("epicId").getAsInt();
                task = new Subtask(name, description, status, startTime, duration, epicId);
                break;
            default:
                throw new JsonParseException("Неизвестный тип задачи: " + type);
        }

        task.setId(id);
        return task;
    }
}
