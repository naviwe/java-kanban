package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHandler {
    public EpicHandler(TaskManager taskManager) {
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
                    String jsonString = gson.toJson(taskManager.getEpicsList());
                    System.out.println("Получены эпики: " + jsonString);
                    response = gson.toJson(jsonString);
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Epic epic = taskManager.getEpicByIdNumber(id);
                        if (epic != null) {
                            statusCode = 200;
                            response = gson.toJson(epic);
                        } else {
                            statusCode = 404;
                            exchange.sendResponseHeaders(statusCode, 0);
                            response = "Эпик с данным id не найден";
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
                    Epic epic = gson.fromJson(bodyRequest, Epic.class);
                    Integer id = epic.getId();
                    if (epic != null) {
                        taskManager.updateTask(epic);
                        statusCode = 200;
                        response = "Эпик с id=" + id + " обновлён";
                    } else {
                        System.out.println("Создан новый эпик");
                        Epic epicCreated = taskManager.addEpic(epic);
                        System.out.println("Созданный эпик: " + epicCreated);
                        int idCreated = epicCreated.getId();
                        statusCode = 201;
                        response = "Создан эпик с id=" + idCreated;
                    }
                } catch (JsonSyntaxException e) {
                    statusCode = 400;
                    response = "Некорректный формат запроса";
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.deleteEpics();
                    statusCode = 200;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.deleteEpicById(id);
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

