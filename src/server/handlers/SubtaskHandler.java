package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;

public class SubtaskHandler extends BaseHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode;
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = 200;
                    response = gson.toJson(taskManager.getSubtaskList());
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Subtask subtask = taskManager.getSubtaskByIdNumber(id);
                        if (subtask != null) {
                            statusCode = 200;
                            response = gson.toJson(subtask);
                        } else {
                            statusCode = 404;
                            exchange.sendResponseHeaders(statusCode, 0);
                            response = "Подзадача с данным id не найдена";
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует корректный параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Некорректный формат id";
                    }
                }
                break;
            case "POST":
                String bodyRequest = readText(exchange);
                if (bodyRequest.isEmpty()) {
                    statusCode = 400;
                    exchange.sendResponseHeaders(statusCode, 0);
                    return;
                }
                try {
                    Subtask subtask = gson.fromJson(bodyRequest, Subtask.class);
                    Integer id = subtask.getId();
                    if (subtask != null) {
                        taskManager.updateTask(subtask);
                        statusCode = 200;
                        response = "Подзадача с id=" + id + " обновлена";
                    } else {
                        System.out.println("Создана новая подзадача");
                        Subtask subtaskCreated = taskManager.addSubtask(subtask);
                        System.out.println("Созданная подзадача: " + subtaskCreated);
                        int idCreated = subtaskCreated.getId();
                        statusCode = 201;
                        response = "Создана подзадача с id=" + idCreated;
                    }
                } catch (JsonSyntaxException e) {
                    response = "Некорректный формат запроса";
                    statusCode = 400;
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.deleteSubtask();
                    statusCode = 200;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.deleteSubtaskById(id);
                        statusCode = 200;
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует корректный параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Некорректный формат id";
                    }
                }
                break;
            default:
                statusCode = 405;
                response = "Некорректный запрос";
        }

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
        exchange.sendResponseHeaders(statusCode, 0);
        writers(exchange);
    }
}

