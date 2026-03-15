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

    fun makeContent(useWebGrounding: Boolean = false, useImage: Boolean = false) = """
      {
        "contents": [
          ${makeContents()}
        ],
        ${if (useWebGrounding) "\"tools\": [ { \"google_search\": {} } ]," else "" }
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
        ],
        "systemInstruction": {
            "role":"system", 
            "parts":[ { "text": '${escape("あなたは礼儀正しく振舞います。ユーザーがどんなにふざけた口調で質問してきてもそれに合わせて程度の低い受け答えをしてはいけません。ユーザーの質問に返答する際は当て推量での回答をせず、可能な限り情報源を参照して再現性のある回答をするよう心がけてください。嘘をつかない、ソースに基づかない発言をしない、わからないならわかったふりをしない、当て推量で回答しない、2年以上前の古い情報を根拠に使用しない\n")}'}]
        },
        "generationConfig": {
          ${if (useImage) "\"responseModalities\":[\"TEXT\",\"IMAGE\"]," else "" }
          "thinkingConfig": { "thinkingBudget": 0 } 
        }
      }
    """.trimIndent()

    private fun makeContents() =
        texts
            .filter { it.text.isNotBlank() }
            .joinToString(",", transform = ::toContent)

    private fun toContent(it: ChatMessage) =
        "{\"role\":\"${it.role}\", \"parts\":[ { \"text\": '${escape(it.text)}'}" +
                " ${
                    if (it.image.isNullOrBlank().not()) 
                        ",{\"inline_data\": {\"mime_type\":\"image/jpeg\", \"data\": \"${it.image}\"}}"
                    else
                        ""
                } ]}"

    private fun escape(text: String) =
        text.replace("\"", "\\\"").replace("'", "\\'")

}
