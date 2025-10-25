/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.chat

data class Chat(private val texts: MutableList<ChatMessage> = mutableListOf()) {

    fun list(): List<ChatMessage> = texts

    fun makeContent(useImage: Boolean = false) = """
      {
        "contents": [
          ${makeContents()}
        ],
        ${if (useImage) "\"generationConfig\":{\"responseModalities\":[\"TEXT\",\"IMAGE\"]}," else "" }
        "safetySettings": [
            {
                "category": "HARM_CATEGORY_DANGEROUS_CONTENT",
                "threshold": "BLOCK_ONLY_HIGH"
            },
            {
                "category": "HARM_CATEGORY_HARASSMENT",
                "threshold": "BLOCK_ONLY_HIGH"
            },
            {
                "category": "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                "threshold": "BLOCK_ONLY_HIGH"
            }
        ]
      }
    """.trimIndent()

    private fun makeContents() =
        texts
            .filter { it.text.isNotBlank() }
            .joinToString(",", transform = ::toContent)

    private fun toContent(it: ChatMessage) =
        "{\"role\":\"${it.role}\", \"parts\":[ { \"text\": '${
            escape(it.text)
        }'}" +
                " ${
                    if (it.image.isNullOrBlank().not()) 
                        ",{\"inline_data\": {\"mime_type\":\"image/jpeg\", \"data\": \"${it.image}\"}}"
                    else
                        ""
                } ]}"

    private fun escape(text: String) =
        text.replace("\"", "\\\"").replace("'", "\\'")

}