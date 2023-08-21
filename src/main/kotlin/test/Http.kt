package test

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import http.constants.HttpMethod
import http.request.RequestBase
import http.request.RequestBaseJson

abstract class Http {

    // создается новый экземпляр ObjectMapper. Он является основным классом либы Jackson для преобразования объектов Java в JSON и обратно
    protected val mapper = ObjectMapper()
        // регистрация модуля Kotlin в ObjectMapper. Модуль предоставляет доп поддержку для работы с Kotlin-специфичными типами данных, такими как `data class`, `enum class` и т д
        .registerModule(KotlinModule.Builder().build())

        // устанавливается конфигурация для ObjectMapper. В данном случае устанавливается конфигурация FAIL_ON_UNKNOWN_PROPERTIES и значение false.
        // Это означает, что при десериализации JSON в объект, если в JSON встречается неизвестное свойство, то процесс десериализации не будет прерван,
        // а неизвестные свойства будут игнорироваться
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    companion object {
        const val TASK_ADD = "/task/add"
        const val TASK_ADD2 = "/task/add2"
        const val TASK_LIST = "/task/list"
        const val TASK_LIST2 = "/task/list2"
        const val TASK_LIST3 = "/task/list3"
        const val TASK_LIST4 = "/task/list4"
        const val TASK_UPDATE = "/task/update"
        const val TASK_DELETE = "/task/delete"
    }

    fun createRequest(method: HttpMethod, host: String, url: String): RequestBase<Nothing> {
        return object : RequestBaseJson<Nothing>(method, host, url) {}
    }
}
