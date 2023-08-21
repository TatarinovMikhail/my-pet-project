package http.request

import com.fasterxml.jackson.databind.ObjectMapper
import http.constants.HttpMethod
import http.constants.ApiHeaders.Companion.APPLICATION_JSON
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

abstract class RequestBaseJson<T>(method: HttpMethod, host: String, path: String) :
    RequestBase<T>(method, host, path) {

    override val contentType = APPLICATION_JSON

    override fun build(): RequestBody = ObjectMapper().writeValueAsBytes(body).toRequestBody()

    override fun bodyToString(): String = ObjectMapper().writeValueAsString(body)
}