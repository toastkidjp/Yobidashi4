package jp.toastkid.yobidashi4.domain.service.editor.text

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class JsonPrettyPrint {

    operator fun invoke(jsonString: String): String {
        try {
            val json = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
            val jsonElement = Json.parseToJsonElement(jsonString)
            return json.encodeToString(jsonElement)
        } catch (ignored: SerializationException) {
            return jsonString
        }
    }

}