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
  "generationConfig":{"responseModalities":["TEXT","IMAGE"]},
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
  "generationConfig":{"thinkingConfig":{"thinkingBudget":0}}
}""".trimIndent().replace(" ", "").replace("\n", ""),
            chat.makeContent(useImage = true).replace(" ", "").replace("\n", "")
        )
    }

}