package http.response

import okhttp3.Response

class ResponseNull(response: Response): ResponseBase<Nothing>(response) {

    init {
        logResponse()
    }
    override fun appendBodyAsString(builder: StringBuilder) {
        builder.append("BODY: null\n")
    }
}