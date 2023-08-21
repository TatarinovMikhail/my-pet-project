package http

import http.constants.HttpMethod
import http.constants.ApiHeaders
import http.request.RequestBase
import http.response.*
import io.qameta.allure.Allure
import okhttp3.*
import java.io.InterruptedIOException

class ApiClient() {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .build()

    fun execute(apiRequest: () -> RequestBase<*>): ResponseBase<*> {
        val request = apiRequest() // выполняется функция переданный как аргумент в execute и сохраняется значение в request с типом ApiRequestBase
        val url = constructRequestUrl(request) // достаем нужный хост для отправки запроса
        return make(request, url) // выполняем функцию make
    }

    private fun make(request: RequestBase<*>, url: String): ResponseBase<*> {
        val response = sendRequest(request, url) // выполняем запрос в мок-сервер
        return try { // пытаемся выполнить то, что нам нужно
            val type = if (response.body?.contentType() != null)
                "${response.body?.contentType()?.type}/${response.body?.contentType()?.subtype}" // распарсиваем тип контента запроса
            else null // если нулл, то задаем такое значение в type

            if (type == ApiHeaders.APPLICATION_JSON) {
                ResponseJson(response)
            } else {
                ResponseNull(response)
            }

        } catch (e: InterruptedIOException) { // если не получилось и мы поймали исключение InterruptedIOException
            throw RuntimeException( // то создаем новое исключение RuntimeException
                "Запрос ${request.method} $url не был выполнен: превышено время ожидания ответа", // и добавляем текст нашей ошибки
                e // а также передаем исходное исключение InterruptedIOException в качестве причины,
                //  и выбрасывается исключение RuntimeException в виде возвращаемого типа функции make
            )
        }
    }

    private fun constructRequestUrl(request: RequestBase<*>): String {
        var result = request.host + request.path
        request.pathParams.forEach {
            result = result.replace("{${it.key}}", it.value)
        }
        if (request.queryParams.isNotEmpty()) {
            result += "?"
            request.queryParams.forEach {
                result += "${it.key}=${it.value}&"
            }
        }
        return result.removeSuffix("&")
    }

    private fun logRequest(request: RequestBase<*>, url: String, contentType: MediaType?) {
        val builder = StringBuilder()
        builder.append(
            "REQUEST: ${request.method} $url\n" +
                    "HEADERS:\n"
        )

        request.headers.forEach { (k, v) ->
            builder.append("".prependIndent())
            builder.append("$k: $v\n")
        }

        builder.append("".prependIndent())
        builder.append("Content-type: ${contentType}\n")

        builder.append(
            "BODY:\n" +
                    request.bodyToString().prependIndent()
        )

        Allure.addAttachment("Request", builder.toString())
        println(builder.toString() + "\n")
    }

    private fun sendRequest(request: RequestBase<*>, url: String): Response {
        val body: RequestBody?
        if (request.method == HttpMethod.GET) {
            body = null
        } else
            body = request.build()

        logRequest(request, url, body?.contentType())

        val reqBuilder = Request.Builder()
            .url(url)
            .method(request.method.name, body)

        request.headers.forEach { (k, v) ->
            reqBuilder.header(k, v)
        }

        return client.newCall(reqBuilder.build()).execute()
    }

}
