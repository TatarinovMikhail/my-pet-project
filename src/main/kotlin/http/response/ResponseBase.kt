package http.response

import io.qameta.allure.Allure
import okhttp3.Response
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue

abstract class ResponseBase<T>(val response: Response) {
    var body: T? = null
    val headers: Map<String, List<String>> = response.headers.toMultimap()
    val code: Int = response.code
    val statusMessage: String = response.message

    fun asJson(): ResponseJson {
        if (this is ResponseJson) return this
        else throw AssertionError(
            "Хотели получить ответ как json, но он имеет тип ${
                response.body?.contentType().toString()
            }"
        )
    }

    fun checkStatusCode(statusCode: Int) {
        assertTrue(code == statusCode, "ожидали статус ответа $statusCode, но получили $code")
    }

    fun checkStatusMessage(msg: String) {
        assertTrue(statusMessage == msg, "ожидали текст статуса ответа $msg, но получили $statusMessage")
    }

    fun assertEqualsBase(expected: Any?, actual: Any?){
        Assertions.assertEquals(expected, actual)
    }

    protected fun logResponse() {
        val builder = StringBuilder()
        builder.append(
            "RESPONSE: $code $statusMessage\n" +
                    "HEADERS:\n"
        )

        headers.forEach { (k, v) ->
            builder.append("".prependIndent())
            builder.append("$k: $v\n")
        }

        appendBodyAsString(builder)

        Allure.addAttachment("Response", builder.toString())
        println(builder.toString() + "\n")
    }
    abstract fun appendBodyAsString(builder: StringBuilder)
}
