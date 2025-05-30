package jp.toastkid.yobidashi4.domain.model.chat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
        chat.addUserText("test")
        chat.addModelText("test")

        val list = chat.list()
        assertEquals(2, list.size)
        assertEquals("user", list[0].role)
        assertEquals("model", list[1].role)
    }

    @Test
    fun addModelTextsWithEmptyList() {
        chat.addModelText("Greed")
        chat.addModelText(" is good.")

        assertEquals("Greed is good.", chat.list().last().text)
    }

    @Test
    fun addModelImage() {
        chat.addModelText("With image")
        chat.addModelImage("image")

        assertEquals(1, chat.list().size)
    }

    @Test
    fun addModelImageIfTextsIsEmptyCase() {
        chat.addModelImage("image")

        assertTrue(chat.list().isEmpty())
    }

    @Test
    fun makeContent() {
        chat.addUserText("Test \"is\" good. It's good.")
        chat.addModelText("Answer")

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
  ]
}""".trimIndent().replace(" ", "").replace("\n", ""),
            chat.makeContent().replace(" ", "").replace("\n", "")
        )
    }


    @Test
    fun makeContentWithImage() {
        chat.addUserText("Test \"is\" good. It's good.")
        chat.addModelText("Answer")
        chat.addModelImage("Image")

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
  ]
}""".trimIndent().replace(" ", "").replace("\n", ""),
            chat.makeContent().replace(" ", "").replace("\n", "")
        )
    }

    @Test
    fun makeContentWithImageAndFlag() {
        chat.addUserText("Test \"is\" good. It's good.")
        chat.addModelText("Answer")
        chat.addModelImage("Image")

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
  ]
}""".trimIndent().replace(" ", "").replace("\n", ""),
            chat.makeContent(true).replace(" ", "").replace("\n", "")
        )
    }

}