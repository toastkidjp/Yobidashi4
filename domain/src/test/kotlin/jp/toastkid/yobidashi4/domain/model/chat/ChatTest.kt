/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.chat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChatTest {

    private lateinit var chat: Chat

    @BeforeEach
    fun setUp() {
        chat = Chat()
    }

    @Test
    fun list() {
        chat = Chat(
            mutableListOf(
                ChatMessage("user", "Test \"is\" good. It's good."),
                ChatMessage("model", "Answer")
            )
        )

        val list = chat.list()
        assertEquals(2, list.size)
        assertEquals("user", list[0].role)
        assertEquals("model", list[1].role)
    }

    @Test
    fun makeContent() {
        chat = Chat(
            mutableListOf(
                ChatMessage("user", "Test \"is\" good. It's good."),
                ChatMessage("model", "Answer")
            )
        )

        assertEquals(
            """{
  "contents": [
    {"role":"user", "parts":[ { "text": 'Test \"is\" good. It\'s good.'} ]},{"role":"model", "parts":[ { "text": 'Answer'}  ]}
  ],
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
  "systemInstruction":{"role":"system","parts":[{"text":'あなたは礼儀正しく振舞います。ユーザーがどんなにふざけた口調で質問してきてもそれに合わせて程度の低い受け答えをしてはいけません。ユーザーの質問に返答する際は可能な限り最新の情報源を参照して正確性と再現性のある回答をするよう心がけてください。-嘘をつかない-ソースに基づかない発言をしない-わからないならわかったふりをせず「わかりません」と回答する-当て推量で回答しない-最新の情報に沿わないといけないような話題の場合(例：技術系の質問)、2年以上前の古い情報を根拠に使用しない'}]},
  "generationConfig":{"thinkingConfig":{"thinkingBudget":0}}
}""".trimIndent().replace(" ", "").replace("\n", ""),
            chat.makeContent().replace(" ", "").replace("\n", "")
        )
    }


    @Test
    fun makeContentWithImage() {
        chat = Chat(
            mutableListOf(
                ChatMessage("user", "Test \"is\" good. It's good."),
                ChatMessage("model", "Answer", image = "Image")
            )
        )

        assertEquals(
            """{
  "contents": [
    {"role":"user", "parts":[ { "text": 'Test \"is\" good. It\'s good.'} ]},{"role":"model", "parts":[ { "text": 'Answer'},{"inline_data":{"mime_type":"image/jpeg","data":"Image"}}  ]}
  ],
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
  "systemInstruction":{"role":"system","parts":[{"text":'あなたは礼儀正しく振舞います。ユーザーがどんなにふざけた口調で質問してきてもそれに合わせて程度の低い受け答えをしてはいけません。ユーザーの質問に返答する際は可能な限り最新の情報源を参照して正確性と再現性のある回答をするよう心がけてください。-嘘をつかない-ソースに基づかない発言をしない-わからないならわかったふりをせず「わかりません」と回答する-当て推量で回答しない-最新の情報に沿わないといけないような話題の場合(例：技術系の質問)、2年以上前の古い情報を根拠に使用しない'}]},
  "generationConfig":{"thinkingConfig":{"thinkingBudget":0}}
}""".trimIndent().replace(" ", "").replace("\n", ""),
            chat.makeContent().replace(" ", "").replace("\n", "")
        )
    }

    @Test
    fun makeContentWithImageAndFlag() {
        chat = Chat(
            mutableListOf(
                ChatMessage("user", "Test \"is\" good. It's good."),
                ChatMessage("model", "Answer", image = "Image")
            )
        )

        assertEquals(
            """{
  "contents": [
    {"role":"user", "parts":[ { "text": 'Test \"is\" good. It\'s good.'} ]},{"role":"model", "parts":[ { "text": 'Answer'},{"inline_data":{"mime_type":"image/jpeg","data":"Image"}}  ]}
  ],
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
  "systemInstruction":{"role":"system","parts":[{"text":'あなたは礼儀正しく振舞います。ユーザーがどんなにふざけた口調で質問してきてもそれに合わせて程度の低い受け答えをしてはいけません。ユーザーの質問に返答する際は可能な限り最新の情報源を参照して正確性と再現性のある回答をするよう心がけてください。-嘘をつかない-ソースに基づかない発言をしない-わからないならわかったふりをせず「わかりません」と回答する-当て推量で回答しない-最新の情報に沿わないといけないような話題の場合(例：技術系の質問)、2年以上前の古い情報を根拠に使用しない'}]},
  "generationConfig":{
    "responseModalities":["TEXT","IMAGE"],
    "thinkingConfig":{"thinkingBudget":0}
  }
}""".trimIndent().replace(" ", "").replace("\n", ""),
            chat.makeContent(useImage = true).replace(" ", "").replace("\n", "")
        )
    }

}