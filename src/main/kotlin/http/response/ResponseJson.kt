package http.response

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.Response
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue

class ResponseJson(response: Response) : ResponseBase<JsonNode>(response) {

    val mapper: ObjectMapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    init {
        body = if (response.body?.source()?.isOpen == true) {
            mapper.readTree(response.body?.string())
        } else null
        logResponse()
    }

    // достаем значение по пути {path}
    inline fun <reified T> getAs(): T {
        return try {
            val listType = object : TypeReference<T>() {}
            mapper.readValue(body.toString(), listType)
        } catch (exc: MismatchedInputException) {
            throw AssertionError()
        }
    }

    fun checkID(expectedID: Int, actualID: Int) {
        assertTrue(expectedID == actualID, "ожидали id '$expectedID', но получили '$actualID'")
    }

    fun checkName(expectedName: String, actualName: String) {
        assertTrue(expectedName == actualName, "ожидали name '$expectedName', но получили '$actualName'")
    }

    fun assertEqualsJson(expected: Any?, actual: Any?){
        Assertions.assertEquals(expected, actual)
    }

    override fun appendBodyAsString(builder: StringBuilder) {
        builder.append("BODY:\n")
        builder.append(body?.toPrettyString()?.prependIndent())
    }
}
