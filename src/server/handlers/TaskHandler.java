package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        int statusCode;
        String method = httpExchange.getRequestMethod();
        String path = String.valueOf(httpExchange.getRequestURI());

        System.out.println("Обрабатывается запрос " + path + " с методом " + method);

        switch (method) {
            case "GET":
                String query = httpExchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = 200;
                    String jsonString = gson.toJson(taskManager.getTasksList());
                    System.out.println("GET TASKS: " + jsonString);
                    response = gson.toJson(jsonString);
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Task task = taskManager.getTaskByIdNumber(id);
                        if (task != null) {
                            statusCode = 200;
                            response = gson.toJson(task);
                        } else {
                            statusCode = 404;
                            response = "Задача с данным id не найдена";
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует обязательный параметр id";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Некорректный формат id";
                    }
                }
                break;
            case "POST":
                String bodyRequest = readText(httpExchange);
                if (bodyRequest.isEmpty()) {
                    statusCode = 400;
                    httpExchange.sendResponseHeaders(statusCode, 0);
                    return;
                }
                try {
                    Task task = gson.fromJson(bodyRequest, Task.class);
                    Integer id = task.getId();
                    if (task != null) {
                        taskManager.updateTask(task);
                        statusCode = 201;
                        response = "Задача с id=" + id + " обновлена";
                    } else {
                        Task taskCreated = taskManager.addTask(task);
                        System.out.println("CREATED TASK: " + taskCreated);
                        int idCreated = taskCreated.getId();
                        statusCode = 201;
                        response = "Создана задача с id=" + idCreated;
                    }
                } catch (JsonSyntaxException e) {
                    statusCode = 400;
                    response = "Некорректный формат запроса";
                }
                break;
            case "DELETE":
                response = "";
                query = httpExchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.deleteTasks();
                    statusCode = 200;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.deleteTaskById(id);
                        statusCode = 200;
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "В запросе отсутствует обязательный параметр id";
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

        httpExchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET);
        httpExchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        writers(httpExchange);
    }
}
