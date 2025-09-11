/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
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