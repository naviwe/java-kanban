import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.*;
import server.HttpTaskServer;
import server.KVServer;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private static KVServer kvServer;
    private final Gson gson = Managers.getGson();

    private TaskManager taskManager;

    private Task taskOne;
    private Task taskTwo;
    private Task taskThree;
    private Task taskFour;
    private Epic epicOne;
    private Epic epicTwo;
    private Subtask subtaskOne;
    private Subtask subtaskTwo;
    private Subtask subtaskThree;

    @BeforeAll
    static void beforeAll() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @BeforeEach
    void init() throws IOException, InterruptedException {
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();


        taskOne = new Task("Задача №1", "Это простая задача", Status.NEW);
        taskManager.addTask(taskOne);

        taskTwo = new Task("Задача №2", "Еще одна простая задача", Status.NEW);
        taskManager.addTask(taskTwo);

        taskThree = new Task("Задача №3", "У этой задачи есть время и продолжительность", Status.NEW);
        taskThree.setStartTime(LocalDateTime.of(2023, 6, 22, 12, 10));
        taskThree.setDuration(Duration.ofMinutes(120));
        taskManager.addTask(taskThree);

        taskFour = new Task("Задача №4", "У этой задачи есть время и продолжительность", Status.NEW);
        taskFour.setStartTime(LocalDateTime.of(2023, 6, 22, 10, 30));
        taskFour.setDuration(Duration.ofMinutes(60));
        taskManager.addTask(taskFour);

        epicOne = new Epic("Эпик №1", "У этого эпика 3 подзадачи");
        taskManager.addEpic(epicOne);

        epicTwo = new Epic("Эпик №2", "У этого эпика нет подзадач");
        taskManager.addEpic(epicTwo);

        subtaskOne = new Subtask("Подзадача 1.1", "эта подзадача " +
                "принадлежит эпику №1", Status.NEW, epicOne.getId());
        taskManager.addSubtask(subtaskOne);

        subtaskTwo = new Subtask("Подзадача 1.2", "эта подзадача " +
                "принадлежит эпику №1", Status.NEW, epicOne.getId());
        taskManager.addSubtask(subtaskTwo);

        subtaskThree = new Subtask("Подзадача 1.3", "эта подзадача " +
                "принадлежит эпику №1", Status.NEW, epicOne.getId());
        subtaskThree.setStartTime(LocalDateTime.of(2023, 6, 23, 14, 7));
        subtaskThree.setDuration(Duration.ofMinutes(30));
        taskManager.addSubtask(subtaskThree);
    }

    @AfterEach
    void tearDown() {
        taskManager.deleteTasks();
        taskManager.deleteSubtask();
        taskManager.deleteEpics();
        httpTaskServer.stop();
    }

    @AfterAll
    static void afterAll() {
        kvServer.stop();
    }


    @Test
    void getTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskListType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        ArrayList<Task> taskList = gson.fromJson(response.body(), taskListType);

        assertNotNull(taskList, "Список задач не возвращен");
        assertEquals(4, taskList.size(), "Неверное количество задач");
        assertEquals(taskFour, taskList.get(3), "Задачи не совпадают");
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type epicListType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        ArrayList<Epic> epicList = gson.fromJson(response.body(), epicListType);

        assertNotNull(epicList, "Список задач не возвращен");
        assertEquals(2, epicList.size(), "Неверное количество задач");
        assertEquals(epicTwo, epicList.get(1), "Задачи не совпадают");
    }

    @Test
    void getSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type subtaskListType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        ArrayList<Subtask> subtaskList = gson.fromJson(response.body(), subtaskListType);

        assertNotNull(subtaskList, "Список задач не возвращен");
        assertEquals(3, subtaskList.size(), "Неверное количество задач");
        assertEquals(subtaskThree, subtaskList.get(2), "Задачи не совпадают");
    }

    @Test
    void getSubtaskListByEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/subtasklist/?id=5");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type subtaskListType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        ArrayList<Subtask> subtaskList = gson.fromJson(response.body(), subtaskListType);

        assertNotNull(subtaskList, "Список задач не возвращен");
        assertEquals(3, subtaskList.size(), "Неверное количество задач");
        assertEquals(subtaskThree, subtaskList.get(2), "Задачи не совпадают");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        taskManager.getTaskByIdNumber(1);
        taskManager.getTaskByIdNumber(2);
        taskManager.getEpicByIdNumber(5);
        taskManager.getEpicByIdNumber(6);
        taskManager.getSubtaskByIdNumber(7);
        taskManager.getSubtaskByIdNumber(8);
        taskManager.getSubtaskByIdNumber(9);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type historyListType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        ArrayList<Task> historyList = gson.fromJson(response.body(), historyListType);


        assertNotNull(historyList, "Список истории просмотров не возвращен");
        assertEquals(7, historyList.size(), "Неверное количество задач в истории просмотров");
        assertEquals(epicOne, historyList.get(2), "Задачи не совпадают");
        assertEquals(subtaskThree, historyList.get(6), "Задачи не совпадают");
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type prioritizedtaskListType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        ArrayList<Task> PrioritizedTaskList = gson.fromJson(response.body(), prioritizedtaskListType);

        assertNotNull(PrioritizedTaskList, "Список задач не возвращен");
        assertEquals(7, PrioritizedTaskList.size(), "Неверное количество задач");
        assertEquals(taskFour, PrioritizedTaskList.get(4), "Задачи не совпадают");
        assertEquals(subtaskTwo, PrioritizedTaskList.get(6), "Задачи не совпадают");
    }

    @Test
    void getTaskByID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task task = gson.fromJson(response.body(), taskType);

        assertNotNull(task, "Задача не возвращена");
        assertEquals(task, taskFour, "Задачи не совпадают");
    }

    @Test
    void getEpicTaskByID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=6");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Epic>() {
        }.getType();
        Epic epic = gson.fromJson(response.body(), taskType);


        assertNotNull(epic, "Задача не возвращена");
        assertEquals(epic, epicTwo, "Задачи не совпадают");
    }

    @Test
    void getSubtaskByID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=9");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Subtask>() {
        }.getType();
        Subtask subtask = gson.fromJson(response.body(), taskType);


        assertNotNull(subtask, "Задача не возвращена");
        assertEquals(subtask, subtaskThree, "Задачи не совпадают");
    }

    @Test
    void createTask() throws IOException, InterruptedException {
        Task taskFive = new Task("Задача №5", "Это простая задача", Status.NEW);
        taskFive.setStartTime(LocalDateTime.of(2023, 7, 21, 12, 12));
        taskFive.setDuration(Duration.ofMinutes(180));
        String taskFiveJson = gson.toJson(taskFive);

        HttpClient client = HttpClient.newHttpClient();
        URI urlForCreateTask = URI.create("http://localhost:8080/tasks/task");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskFiveJson);
        HttpRequest requestForCreateTask = HttpRequest.newBuilder()
                .uri(urlForCreateTask)
                .POST(body)
                .build();
        HttpResponse<String> responseForCreateTask = client.send(requestForCreateTask,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseForCreateTask.statusCode());


        URI url = URI.create("http://localhost:8080/tasks/task/?id=10");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task task = gson.fromJson(response.body(), taskType);
        taskFive.setId(task.getId());

        assertNotNull(task, "Задача не возвращена");
        assertEquals(task, taskFive, "Задачи не совпадают");
    }

    @Test
    void createEpicTask() throws IOException, InterruptedException {
        Epic epicThree = new Epic("Эпик №3", "Это простой эпик");
        epicThree.setStartTime(LocalDateTime.of(2023, 7, 21, 12, 12));
        epicThree.setDuration(Duration.ofMinutes(180));
        String epicThreeJson = gson.toJson(epicThree);

        HttpClient client = HttpClient.newHttpClient();
        URI urlForCreateEpic = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(epicThreeJson);
        HttpRequest requestForCreateEpic = HttpRequest.newBuilder()
                .uri(urlForCreateEpic)
                .POST(body)
                .build();
        HttpResponse<String> responseForCreateEpic = client.send(requestForCreateEpic,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseForCreateEpic.statusCode());


        URI url = URI.create("http://localhost:8080/tasks/epic/?id=10");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type epicType = new TypeToken<Epic>() {
        }.getType();
        Epic epic = gson.fromJson(response.body(), epicType);
        epicThree.setId(epic.getId());


        assertNotNull(epic, "Эпик не возвращен");
        assertEquals(epic, epicThree, "Эпики не совпадают");
    }

    @Test
    void createSubtask() throws IOException, InterruptedException {
        Subtask subtaskFour = new Subtask("Подзадача №4", "эта подзадача " +
                "принадлежит эпику №2", Status.NEW, epicTwo.getId());
        subtaskFour.setStartTime(LocalDateTime.of(2023, 7, 21, 12, 12));
        subtaskFour.setDuration(Duration.ofMinutes(180));
        String SubtaskFourJson = gson.toJson(subtaskFour);

        HttpClient client = HttpClient.newHttpClient();
        URI urlForCreateSubtask = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(SubtaskFourJson);
        HttpRequest requestForCreateSubtask = HttpRequest.newBuilder()
                .uri(urlForCreateSubtask)
                .POST(body)
                .build();
        HttpResponse<String> responseForCreateSubtask = client.send(requestForCreateSubtask,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responseForCreateSubtask.statusCode());


        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=10");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type SubtaskType = new TypeToken<Subtask>() {
        }.getType();
        Subtask subtask = gson.fromJson(response.body(), SubtaskType);
        subtaskFour.setId(subtask.getId());


        assertNotNull(subtask, "Задача не возвращена");
        assertEquals(subtask, subtaskFour, "Задачи не совпадают");
    }

    @Test
    void removeTaskByID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI testURL = URI.create("http://localhost:8080/tasks/task/?id=4");
        HttpRequest testRequest = HttpRequest.newBuilder().uri(testURL).GET().build();
        HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, testResponse.statusCode());
        assertEquals("null", testResponse.body(), "сообщение об ошибке" +
                " некорректно");
    }

    @Test
    void removeEpicTaskByID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=6");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI testURL = URI.create("http://localhost:8080/tasks/epic/?id=6");
        HttpRequest testRequest = HttpRequest.newBuilder().uri(testURL).GET().build();
        HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, testResponse.statusCode());
        assertEquals("null", testResponse.body(), "сообщение об ошибке" +
                " некорректно");
    }

    @Test
    void removeSubtaskByID() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=9");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI testURL = URI.create("http://localhost:8080/tasks/subtask/?id=9");
        HttpRequest testRequest = HttpRequest.newBuilder().uri(testURL).GET().build();
        HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, testResponse.statusCode());
        assertEquals("null", testResponse.body(), "сообщение об ошибке" +
                " некорректно");

    }

    @Test
    void clearTaskList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI testURL = URI.create("http://localhost:8080/tasks/tasks");
        HttpRequest testRequest = HttpRequest.newBuilder().uri(testURL).GET().build();
        HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());

        Type taskListType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        ArrayList<Task> taskList = gson.fromJson(testResponse.body(), taskListType);

        assertNotNull(taskList, "Список задач не возвращен");
        assertTrue(taskList.isEmpty(), "Список задач не пустой");
    }

    @Test
    void clearEpicTaskList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI testURL = URI.create("http://localhost:8080/tasks/epics");
        HttpRequest testRequest = HttpRequest.newBuilder().uri(testURL).GET().build();
        HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());

        Type epicListType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        ArrayList<Epic> epicList = gson.fromJson(testResponse.body(), epicListType);

        assertNotNull(epicList, "Список эпиков не возвращен");
        assertTrue(epicList.isEmpty(), "Список подзадач не пустой");
    }

    @Test
    void clearSubtaskList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        URI testURL = URI.create("http://localhost:8080/tasks/subtasks");
        HttpRequest testRequest = HttpRequest.newBuilder().uri(testURL).GET().build();
        HttpResponse<String> testResponse = client.send(testRequest, HttpResponse.BodyHandlers.ofString());

        Type subtaskListType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        ArrayList<Subtask> subtaskList = gson.fromJson(testResponse.body(), subtaskListType);


        assertNotNull(subtaskList, "Список подзадач не возвращен");
        assertTrue(subtaskList.isEmpty(), "Список подзадач не пустой");
    }

}