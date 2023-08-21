package http.constants

enum class HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE
}

class ApiHeaders {
    companion object {
        const val APPLICATION_JSON = "application/json"
        const val MULTIPART_FORM_DATA = "multipart/form-data"
        const val URL_ENCODED = "application/x-www-form-urlencoded"
        const val TEXT_PLAIN = "text/plain"
    }
}
