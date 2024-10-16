package manager.adapters;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import task.Subtask;
import task.Status;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskAdapter extends TypeAdapter<Subtask> {

    @Override
    public void write(JsonWriter jsonWriter, Subtask subtask) throws IOException {
        if (subtask == null) {
            jsonWriter.nullValue();
            return;
        }

        jsonWriter.beginObject();
        jsonWriter.name("id").value(subtask.getId());
        jsonWriter.name("name").value(subtask.getName());
        jsonWriter.name("description").value(subtask.getDescription());
        jsonWriter.name("status").value(subtask.getStatus().name());
        jsonWriter.name("type").value(subtask.getType().name());

        if (subtask.getStartTime() != null) {
            jsonWriter.name("startTime").value(subtask.getStartTime().format(LocalDateTimeAdapter.FORMATTER));
        } else {
            jsonWriter.name("startTime").nullValue();
        }

        if (subtask.getDuration() != null) {
            jsonWriter.name("duration").value(subtask.getDuration().toMinutes());
        } else {
            jsonWriter.name("duration").nullValue();
        }

        jsonWriter.name("epicId").value(subtask.getEpicId());

        jsonWriter.endObject();
    }

    @Override
    public Subtask read(JsonReader jsonReader) throws IOException {
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

        int epicId = jsonObject.get("epicId").getAsInt();

        Subtask subtask = new Subtask(name, description, status, startTime, duration, epicId);
        subtask.setId(id);

        return subtask;
    }
}
