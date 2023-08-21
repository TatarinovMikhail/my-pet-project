package test

import http.ApiClient
import http.constants.ApiHeaders
import http.constants.HttpMethod
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import test.TestConfig.HOST_8080
import test.TestConfig.LOCALHOST_8080
import test.model.Task
import test.model.Tasks
import java.io.FileInputStream

class AppTask : Http() {
    private val apiClient: ApiClient = ApiClient()
    private lateinit var server: MockWebServer

    private val taskAddDto = mapper.readValue(FileInputStream("src/main/resources/addFirstTask.json"), Task::class.java)
    private val taskAddBody = mapper.writeValueAsString(taskAddDto)

    private val taskAddDto2 = mapper.readValue(FileInputStream("src/main/resources/addSecondTask.json"), Task::class.java)
    private val taskAddBody2 = mapper.writeValueAsString(taskAddDto2)

    private val taskListDto = mapper.readValue(FileInputStream("src/main/resources/listTask.json"), Tasks::class.java)
    private val taskListBody = mapper.writeValueAsString(taskListDto)

    private val taskListDto2 = mapper.readValue(FileInputStream("src/main/resources/listTask2.json"), Tasks::class.java)
    private val taskListBody2 = mapper.writeValueAsString(taskListDto2)

    private val taskListDto3 = mapper.readValue(FileInputStream("src/main/resources/listTask3.json"), Tasks::class.java)
    private val taskListBody3 = mapper.writeValueAsString(taskListDto3)

    private val taskListDto4 = mapper.readValue(FileInputStream("src/main/resources/listTask4.json"), Tasks::class.java)
    private val taskListBody4 = mapper.writeValueAsString(taskListDto4)

    private val taskUpdateDto = mapper.readValue(FileInputStream("src/main/resources/updateFirstTask.json"), Task::class.java)
    private val taskUpdateBody = mapper.writeValueAsString(taskUpdateDto)

    private val taskDeleteDto = mapper.readValue(FileInputStream("src/main/resources/deleteTask.json"), Task::class.java)
    private val taskDeleteBody = mapper.writeValueAsString(taskDeleteDto)

    @BeforeEach
    fun setUp() {
        server = MockWebServer()

        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                when (request.method) {
                    HttpMethod.GET.toString() -> {
                        when (request.path) {
                            TASK_LIST -> return MockResponse()
                                .setResponseCode(200)
                                .setBody(taskListBody)
                                .setHeader("Content-type", ApiHeaders.APPLICATION_JSON)
                            TASK_LIST2 -> return MockResponse()
                                .setResponseCode(200)
                                .setBody(taskListBody2)
                                .setHeader("Content-type", ApiHeaders.APPLICATION_JSON)
                            TASK_LIST3 -> return MockResponse()
                                .setResponseCode(200)
                                .setBody(taskListBody3)
                                .setHeader("Content-type", ApiHeaders.APPLICATION_JSON)
                            TASK_LIST4 -> return MockResponse()
                                .setResponseCode(200)
                                .setBody(taskListBody4)
                                .setHeader("Content-type", ApiHeaders.APPLICATION_JSON)
                        }
                    }
                    HttpMethod.POST.toString() -> {
                        when (request.path) {
                            TASK_ADD -> return MockResponse()
                                .setResponseCode(200)
                                .setBody(taskAddBody)
                                .setHeader("Content-type", ApiHeaders.APPLICATION_JSON)
                            TASK_ADD2 -> return MockResponse()
                                .setResponseCode(200)
                                .setBody(taskAddBody2)
                                .setHeader("Content-type", ApiHeaders.APPLICATION_JSON)
                            TASK_UPDATE -> return MockResponse()
                                .setResponseCode(200)
                                .setBody(taskUpdateBody)
                                .setHeader("Content-type", ApiHeaders.APPLICATION_JSON)
                            TASK_DELETE -> return MockResponse()
                                .setResponseCode(200)
                                .setBody(taskDeleteBody)
                                .setHeader("Content-type", ApiHeaders.APPLICATION_JSON)
                        }
                    }
                }

                return MockResponse().setResponseCode(500)
            }
        }

        server.dispatcher = dispatcher
        server.start(HOST_8080)
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun firstTaskAddSuccess() {
        val request = createRequest(HttpMethod.POST,LOCALHOST_8080, TASK_ADD)
        val response = apiClient.execute { request }.asJson()
        val task = response.getAs<Task>()

        response.checkStatusCode(200)
        response.checkStatusMessage("OK")
        response.checkID(1,task.id)
        response.checkName("Сходить за хлебом", task.name)
        response.assertEqualsJson(false, task.isDeleted)
        response.assertEqualsJson("2018-12-10T13:45:00.000Z", task.createdAt)
        response.assertEqualsJson("2018-12-10T13:45:00.000Z", task.updatedAt)

    }

    @Test
    fun taskAddInvalidMethod() {
        val request = createRequest(HttpMethod.GET,LOCALHOST_8080, TASK_ADD)
        val response = apiClient.execute { request }

        response.assertEqualsBase(500, response.code)
        response.assertEqualsBase("Server Error", response.statusMessage)
        response.assertEqualsBase(null, response.body)
    }

    @Test
    fun taskListSuccess() {
        val request = createRequest(HttpMethod.GET,LOCALHOST_8080, TASK_LIST)
        val response = apiClient.execute { request }.asJson()
        val task = response.getAs<Tasks>()

        response.assertEqualsJson(200, response.code)
        response.assertEqualsJson("OK", response.statusMessage)
        response.assertEqualsJson(1, task.tasks.size)
        response.assertEqualsJson(1, task.tasks[0].id)
        response.assertEqualsJson("Сходить за хлебом", task.tasks[0].name)
        response.assertEqualsJson("2018-12-10T13:45:00.000Z", task.tasks[0].createdAt)
        response.assertEqualsJson("2018-12-10T13:45:00.000Z", task.tasks[0].updatedAt)
    }

    @Test
    fun taskListInvalidMethod() {
        val request = createRequest(HttpMethod.POST,LOCALHOST_8080, TASK_LIST)
        val response = apiClient.execute { request }

        response.assertEqualsBase(500, response.code)
        response.assertEqualsBase("Server Error", response.statusMessage)
        response.assertEqualsBase(null, response.body)
    }

    @Test
    fun secondTaskAddSuccess() {
        val request = createRequest(HttpMethod.POST,LOCALHOST_8080, TASK_ADD2)
        val response = apiClient.execute { request }.asJson()
        val task = response.getAs<Task>()

        response.assertEqualsJson(200,response.code)
        response.assertEqualsJson("OK",response.statusMessage)
        response.assertEqualsJson(2, task.id)
        response.assertEqualsJson("Купить молоко", task.name)
        response.assertEqualsJson(false, task.isDeleted)
        response.assertEqualsJson("2018-12-11T10:42:00.000Z", task.createdAt)
        response.assertEqualsJson("2018-12-11T10:42:00.000Z", task.updatedAt)
    }

    @Test
    fun taskListSuccess2() {
        val request = createRequest(HttpMethod.GET,LOCALHOST_8080, TASK_LIST2)
        val response = apiClient.execute { request }.asJson()
        val task = response.getAs<Tasks>()

        response.assertEqualsJson(200, response.code)
        response.assertEqualsJson("OK", response.statusMessage)
        response.assertEqualsJson(2, task.tasks.size)
        response.assertEqualsJson(1, task.tasks[0].id)
        response.assertEqualsJson("Сходить за хлебом", task.tasks[0].name)
        response.assertEqualsJson("2018-12-10T13:45:00.000Z", task.tasks[0].createdAt)
        response.assertEqualsJson("2018-12-10T13:45:00.000Z", task.tasks[0].updatedAt)

        response.assertEqualsJson(2, task.tasks[1].id)
        response.assertEqualsJson("Купить молоко", task.tasks[1].name)
        response.assertEqualsJson("2018-12-11T10:42:00.000Z", task.tasks[1].createdAt)
        response.assertEqualsJson("2018-12-11T10:42:00.000Z", task.tasks[1].updatedAt)
    }

    @Test
    fun updateFirstTask() {
        val request = createRequest(HttpMethod.POST,LOCALHOST_8080, TASK_UPDATE)
        val response = apiClient.execute { request }.asJson()
        val task = response.getAs<Task>()

        response.assertEqualsJson(200, response.code)
        response.assertEqualsJson("OK", response.statusMessage)
        response.assertEqualsJson(1, task.id)
        response.assertEqualsJson("Отнести ключ", task.name)
        response.assertEqualsJson(false, task.isDeleted)
        response.assertEqualsJson("2018-12-10T13:45:00.000Z", task.createdAt)
        response.assertEqualsJson("2018-12-11T17:42:00.000Z", task.updatedAt)
    }

    @Test
    fun taskListSuccess3() {
        val request = createRequest(HttpMethod.GET,LOCALHOST_8080, TASK_LIST3)
        val response = apiClient.execute { request }.asJson()
        val task = response.getAs<Tasks>()

        response.assertEqualsJson(200, response.code)
        response.assertEqualsJson("OK", response.statusMessage)
        response.assertEqualsJson(2, task.tasks.size)
        response.assertEqualsJson(1, task.tasks[0].id)
        response.assertEqualsJson("Отнести ключ", task.tasks[0].name)
        response.assertEqualsJson("2018-12-10T13:45:00.000Z", task.tasks[0].createdAt)
        response.assertEqualsJson("2018-12-11T17:42:00.000Z", task.tasks[0].updatedAt)

        response.assertEqualsJson(2, task.tasks[1].id)
        response.assertEqualsJson("Купить молоко", task.tasks[1].name)
        response.assertEqualsJson("2018-12-11T10:42:00.000Z", task.tasks[1].createdAt)
        response.assertEqualsJson("2018-12-11T10:42:00.000Z", task.tasks[1].updatedAt)
    }

    @Test
    fun deleteTask() {
        val request = createRequest(HttpMethod.POST,LOCALHOST_8080, TASK_DELETE)
        val response = apiClient.execute { request }.asJson()
        val task = response.getAs<Task>()

        response.assertEqualsJson(200, response.code)
        response.assertEqualsJson("OK", response.statusMessage)
        response.assertEqualsJson(2, task.id)
        response.assertEqualsJson("Купить молоко", task.name)
        response.assertEqualsJson(true, task.isDeleted)
        response.assertEqualsJson("2018-12-11T10:42:00.000Z", task.createdAt)
        response.assertEqualsJson("2018-12-11T10:45:00.000Z", task.updatedAt)
    }

    @Test
    fun taskListSuccess4() {
        val request = createRequest(HttpMethod.GET,LOCALHOST_8080, TASK_LIST4)
        val response = apiClient.execute { request }.asJson()
        val task = response.getAs<Tasks>()

        response.assertEqualsJson(200, response.code)
        response.assertEqualsJson("OK", response.statusMessage)
        response.assertEqualsJson(1, task.tasks.size)
        response.assertEqualsJson(1, task.tasks[0].id)
        response.assertEqualsJson("Отнести ключ", task.tasks[0].name)
        response.assertEqualsJson("2018-12-10T13:45:00.000Z", task.tasks[0].createdAt)
        response.assertEqualsJson("2018-12-11T17:42:00.000Z", task.tasks[0].updatedAt)
    }
}
