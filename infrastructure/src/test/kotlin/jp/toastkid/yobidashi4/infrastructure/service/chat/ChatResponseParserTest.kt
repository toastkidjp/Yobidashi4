package jp.toastkid.yobidashi4.infrastructure.service.chat

import java.nio.charset.StandardCharsets
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChatResponseParserTest {

    private lateinit var subject: ChatResponseParser

    @BeforeEach
    fun setUp() {
        subject = ChatResponseParser()
    }

    @Test
    fun invoke() {
        val parsed = subject.invoke(
            """
{
  "candidates": [
    {
      "content": {
        "parts": [
          {
            "text": "Test result."
          }
        ],
        "role": "model"
      },
      "finishReason": "STOP",
      "index": 0,
      "safetyRatings": [
        {
          "category": "HARM_CATEGORY_SEXUALLY_EXPLICIT",
          "probability": "NEGLIGIBLE"
        },
        {
          "category": "HARM_CATEGORY_HATE_SPEECH",
          "probability": "NEGLIGIBLE"
        },
        {
          "category": "HARM_CATEGORY_HARASSMENT",
          "probability": "NEGLIGIBLE"
        },
        {
          "category": "HARM_CATEGORY_DANGEROUS_CONTENT",
          "probability": "NEGLIGIBLE"
        }
      ]
    }
  ],
  "promptFeedback": {
    "safetyRatings": [
      {
        "category": "HARM_CATEGORY_SEXUALLY_EXPLICIT",
        "probability": "NEGLIGIBLE"
      },
      {
        "category": "HARM_CATEGORY_HATE_SPEECH",
        "probability": "NEGLIGIBLE"
      },
      {
        "category": "HARM_CATEGORY_HARASSMENT",
        "probability": "NEGLIGIBLE"
      },
      {
        "category": "HARM_CATEGORY_DANGEROUS_CONTENT",
        "probability": "NEGLIGIBLE"
      }
    ]
  }
}                
            """.trimIndent().byteInputStream(StandardCharsets.UTF_8)
        )
        assertEquals("Test result.", parsed)
    }

    @Test
    fun irregularCase() {
        val parsed = subject.invoke("\"text\":".byteInputStream(StandardCharsets.UTF_8))

        assertNull(parsed)
    }

}