package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {

    private final int port = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    private final TaskManager taskManager;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(port), 0);
        httpServer.createContext("/tasks", new TasksHandler());
        gson = Managers.getGson();
    }

    public void start() {
        httpServer.start();
        System.out.println("Запустили сервер на порту " + port);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Остановили сервер на порту " + port);
    }

    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {

            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String query = exchange.getRequestURI().getQuery();
                int id = parseId(query);

                switch (method) {
                    case "GET":
                        getHandler(exchange, path, id);
                        break;
                    case "POST":
                        postHandler(exchange, path);
                        break;
                    case "DELETE":
                        deleteHandler(exchange, path, id);
                        break;
                    default:
                        writeResponse(exchange, "Такого эндпоинта не существует", 404);

                }
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                exchange.close();
            }
        }

        private int parseId(String query) {
            if (query != null && query.matches("^id=\\d+$")) {
                String pathId = query.replaceFirst("id=", "");
                try {
                    return Integer.parseInt(pathId);
                } catch (NumberFormatException exception) {
                    return -1;
                }
            } else {
                return -1;
            }
        }

        private void getHandler(HttpExchange exchange, String path, int id) throws IOException {

            switch (path) {
                case "/tasks/tasks": {
                    String json = gson.toJson(taskManager.getTasksList());
                    writeResponse(exchange, json, 200);
                    break;
                }
                case "/tasks/epics": {
                    String json = gson.toJson(taskManager.getEpicsList());
                    writeResponse(exchange, json, 200);
                    break;
                }
                case "/tasks/subtasks": {
                    String json = gson.toJson(taskManager.getSubtaskList());
                    writeResponse(exchange, json, 200);
                    break;
                }
                case "/tasks/epic/subtasklist/": {
                    if (id < 1) {
                        writeResponse(exchange, "значение id некорректно", 400);
                        return;
                    }
                    try {
                        String json = gson.toJson(taskManager.getEpicSubtasksByEpicId(id));
                        writeResponse(exchange, json, 200);
                        break;
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 400);
                        return;
                    }
                }
                case "/tasks/history": {
                    String json = gson.toJson(taskManager.getHistory());
                    writeResponse(exchange, json, 200);
                    break;
                }
                case "/tasks": {
                    String json = gson.toJson(taskManager.getPrioritizedTasks());
                    writeResponse(exchange, json, 200);
                    break;
                }
                case "/tasks/task/": {
                    if (id < 1) {
                        writeResponse(exchange, "значение id некорректно", 400);
                        return;
                    }
                    try {
                        String json = gson.toJson(taskManager.getTaskByIdNumber(id));
                        writeResponse(exchange, json, 200);
                        break;
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 400);
                        return;
                    }
                }
                case "/tasks/epic/": {
                    if (id < 1) {
                        writeResponse(exchange, "значение id некорректно", 400);
                        return;
                    }
                    try {
                        String json = gson.toJson(taskManager.getEpicByIdNumber(id));
                        writeResponse(exchange, json, 200);
                        break;
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 400);
                        return;
                    }
                }
                case "/tasks/subtask/": {
                    if (id < 1) {
                        writeResponse(exchange, "значение id некорректно", 400);
                        return;
                    }
                    try {
                        String json = gson.toJson(taskManager.getSubtaskByIdNumber(id));
                        writeResponse(exchange, json, 200);
                        break;
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 400);
                        return;
                    }
                }
                default:
                    writeResponse(exchange, "Запрашиваемый путь некорректен", 404);
                    break;
            }
        }

        private void postHandler(HttpExchange exchange, String path) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

            switch (path) {
                case "/tasks/task":
                    Task task;
                    try {
                        task = gson.fromJson(body, Task.class);
                    } catch (JsonSyntaxException e) {
                        writeResponse(exchange, "Задача передана некорректно: " + e.getMessage(),
                                400);
                        return;
                    }
                    taskManager.addTask(task);
                    writeResponse(exchange, "Задача успешно создана", 200);
                    break;
                case "/tasks/epic":
                    Epic epic;
                    try {
                        epic = gson.fromJson(body, Epic.class);
                    } catch (JsonSyntaxException e) {
                        writeResponse(exchange, "Задача передана некорректно: " + e.getMessage(),
                                400);
                        return;
                    }
                    taskManager.addEpic(epic);
                    writeResponse(exchange, "Эпик успешно создан", 200);
                    break;
                case "/tasks/subtask":
                    Subtask subtask;
                    try {
                        subtask = gson.fromJson(body, Subtask.class);
                    } catch (JsonSyntaxException e) {
                        writeResponse(exchange, "Задача передана некорректно", 400);
                        return;
                    }
                    taskManager.addSubtask(subtask);
                    writeResponse(exchange, "Подзадача успешно создана", 200);
                    break;
                default:
                    writeResponse(exchange, "Запрашиваемый путь некорректен", 404);
                    break;
            }
        }

        private void deleteHandler(HttpExchange exchange, String path, int id) throws IOException {

            switch (path) {
                case "/tasks/task/": {
                    if (id < 1) {
                        writeResponse(exchange, "значение id некорректно", 400);
                        return;
                    }
                    try {
                        taskManager.deleteTaskById(id);
                        writeResponse(exchange, "Задача " + id + " успешно удалена", 200);
                        break;
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 400);
                        return;
                    }
                }
                case "/tasks/epic/": {
                    if (id < 1) {
                        writeResponse(exchange, "значение id некорректно", 400);
                        return;
                    }
                    try {
                        taskManager.deleteEpicById(id);
                        writeResponse(exchange, "Эпик " + id + " успешно удален", 200);
                        break;
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 400);
                        return;
                    }
                }
                case "/tasks/subtask/": {
                    if (id < 1) {
                        writeResponse(exchange, "значение id некорректно", 400);
                        return;
                    }
                    try {
                        taskManager.deleteSubtaskById(id);
                        writeResponse(exchange, "Подзадача " + id + " успешно удалена", 200);
                        break;
                    } catch (IllegalArgumentException e) {
                        writeResponse(exchange, e.getMessage(), 400);
                        return;
                    }
                }
                case "/tasks/tasks":
                    taskManager.deleteTasks();
                    writeResponse(exchange, "Список задач очищен", 200);
                    break;
                case "/tasks/epics":
                    taskManager.deleteEpics();
                    writeResponse(exchange, "Список эпиков очищен", 200);
                    break;
                case "/tasks/subtasks":
                    taskManager.deleteSubtask();
                    writeResponse(exchange, "Список подзадач очищен", 200);
                    break;
                default:
                    writeResponse(exchange, "Запрашиваемый путь некорректен", 404);
                    break;
            }
        }

        private void writeResponse(HttpExchange exchange,
                                   String responseString,
                                   int responseCode) throws IOException {
            if (responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }
    }
}