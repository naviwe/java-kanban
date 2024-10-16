package manager.adapters;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import task.Epic;
import task.Status;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicAdapter extends TypeAdapter<Epic> {

    @Override
    public void write(JsonWriter jsonWriter, Epic epic) throws IOException {
        if (epic == null) {
            jsonWriter.nullValue();
            return;
        }

        jsonWriter.beginObject();
        jsonWriter.name("id").value(epic.getId());
        jsonWriter.name("name").value(epic.getName());
        jsonWriter.name("description").value(epic.getDescription());
        jsonWriter.name("status").value(epic.getStatus().name());
        jsonWriter.name("type").value(epic.getType().name());

        if (epic.getStartTime() != null) {
            jsonWriter.name("startTime").value(epic.getStartTime().format(LocalDateTimeAdapter.FORMATTER));
        } else {
            jsonWriter.name("startTime").nullValue();
        }

        if (epic.getDuration() != null) {
            jsonWriter.name("duration").value(epic.getDuration().toMinutes());
        } else {
            jsonWriter.name("duration").nullValue();
        }

        jsonWriter.name("subtasksId");
        jsonWriter.beginArray();
        for (Integer subtaskId : epic.getSubtasksId()) {
            jsonWriter.value(subtaskId);
        }
        jsonWriter.endArray();

        if (epic.getEndTime() != null) {
            jsonWriter.name("endTime").value(epic.getEndTime().format(LocalDateTimeAdapter.FORMATTER));
        } else {
            jsonWriter.name("endTime").nullValue();
        }

        jsonWriter.endObject();
    }

    @Override
    public Epic read(JsonReader jsonReader) throws IOException {
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

        List<Integer> subtasksId = new ArrayList<>();
        if (jsonObject.get("subtasksId").isJsonArray()) {
            for (JsonElement element : jsonObject.getAsJsonArray("subtasksId")) {
                subtasksId.add(element.getAsInt());
            }
        }

        LocalDateTime endTime = null;
        if (!jsonObject.get("endTime").isJsonNull()) {
            endTime = LocalDateTime.parse(jsonObject.get("endTime").getAsString(), LocalDateTimeAdapter.FORMATTER);
        }

        Epic epic = new Epic(name, description, status, startTime, duration);
        epic.setId(id);
        epic.setSubtasksId(subtasksId);
        epic.setEndTime(endTime);

        return epic;
    }
}
