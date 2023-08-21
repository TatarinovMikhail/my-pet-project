package http.request

import http.constants.HttpMethod
import okhttp3.RequestBody
import java.nio.charset.Charset

abstract class RequestBase<T>(val method: HttpMethod, val host: String, var path: String, var body: T? = null) {

    var queryParams: MutableMap<String, String> = LinkedHashMap()
    var pathParams: MutableMap<String, String> = mutableMapOf()
    var headers: MutableMap<String, String> = mutableMapOf()
    var charset: Charset? = Charsets.UTF_8

    abstract val contentType: String

    abstract fun build(): RequestBody

    abstract fun bodyToString(): String
}
