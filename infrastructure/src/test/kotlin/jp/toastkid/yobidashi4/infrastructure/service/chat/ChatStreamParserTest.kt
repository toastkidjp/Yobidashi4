package jp.toastkid.yobidashi4.infrastructure.service.chat

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ChatStreamParserTest {

    private lateinit var subject: ChatStreamParser

    private val line = "line: data: {\"candidates\": [{\"content\": {\"parts\": " +
            "[{\"text\": \"**材料 (1人分)**\\n\\n**スープ**\\n* 豚骨または\"}],\"role\": \"model\"},\"finishReason\": " +
            "\"STOP\",\"index\": 0,\"safetyRatings\": [{\"category\": \"HARM_CATEGORY_SEXUALLY_EXPLICIT\"," +
            "\"probability\": \"NEGLIGIBLE\"},{\"category\": \"HARM_CATEGORY_HATE_SPEECH\"," +
            "\"probability\": \"NEGLIGIBLE\"},{\"category\": \"HARM_CATEGORY_HARASSMENT\",\"probability\":" +
            " \"NEGLIGIBLE\"},{\"category\": \"HARM_CATEGORY_DANGEROUS_CONTENT\",\"probability\": \"NEGLIGIBLE\"}]}]," +
            "\"promptFeedback\": {\"safetyRatings\": [{\"category\": \"HARM_CATEGORY_SEXUALLY_EXPLICIT\"," +
            "\"probability\": \"NEGLIGIBLE\"},{\"category\": \"HARM_CATEGORY_HATE_SPEECH\"," +
            "\"probability\": \"NEGLIGIBLE\"},{\"category\": \"HARM_CATEGORY_HARASSMENT\"," +
            "\"probability\": \"NEGLIGIBLE\"},{\"category\": \"HARM_CATEGORY_DANGEROUS_CONTENT\"," +
            "\"probability\": \"NEGLIGIBLE\"}]}}\n"

    @BeforeEach
    fun setUp() {
        subject = ChatStreamParser()
    }

    @Test
    fun invoke() {
        assertEquals("""**材料 (1人分)**

**スープ**
* 豚骨または""".trimIndent(), subject.invoke(line)?.message())
    }

    @Test
    fun unacceptableFormatCase() {
        assertNull(subject.invoke("test"))
    }

    @Test
    fun error() {
        val item =
            subject.invoke("data: {\"candidates\": [{\"finishReason\": \"SAFETY\",\"index\": 0,\"safetyRatings\": [{\"category\": \"HARM_CATEGORY_SEXUALLY_EXPLICIT\",\"probability\": \"NEGLIGIBLE\"},{\"category\": \"HARM_CATEGORY_HATE_SPEECH\",\"probability\": \"NEGLIGIBLE\"},{\"category\": \"HARM_CATEGORY_HARASSMENT\",\"probability\": \"NEGLIGIBLE\"},{\"category\": \"HARM_CATEGORY_DANGEROUS_CONTENT\",\"probability\": \"LOW\"}]}],\"usageMetadata\": {\"promptTokenCount\": 73,\"totalTokenCount\": 73}}\n") ?: fail()

        assertEquals("[ERROR]", item.message())
        assertTrue(item.error())
        assertFalse(item.image())
    }

    @Test
    fun otherError() {
        val item =
            subject.invoke("data: {\"candidates\": [{\"finishReason\": \"OTHER\",\"index\": 0,\"safetyRatings\": [{\"category\": \"HARM_CATEGORY_SEXUALLY_EXPLICIT\",\"probability\": \"NEGLIGIBLE\"},{\"category\": \"HARM_CATEGORY_HATE_SPEECH\",\"probability\": \"LOW\"},{\"category\": \"HARM_CATEGORY_HARASSMENT\",\"probability\": \"LOW\"},{\"category\": \"HARM_CATEGORY_DANGEROUS_CONTENT\",\"probability\": \"NEGLIGIBLE\"}]}],\"usageMetadata\": {\"promptTokenCount\": 1996,\"totalTokenCount\": 1996}}\n") ?: fail()

        assertEquals("[OTHER]", item.message())
        assertTrue(item.error())
        assertFalse(item.image())
    }

    @Test
    fun image() {
        val item =
            subject.invoke("data: {\"candidates\": [{\"content\": {\"parts\": [{\"inlineData\": {\"mimeType\": \"image/png\",\"data\": \"base64encoded-image\" } }],\"role\": \"model\"},\"index\": 0}],\"usageMetadata\": {\"promptTokenCount\": 8,\"candidatesTokenCount\": 1341,\"totalTokenCount\": 1349,\"promptTokensDetails\": [{\"modality\": \"TEXT\",\"tokenCount\": 8}],\"candidatesTokensDetails\": [{\"modality\": \"IMAGE\",\"tokenCount\": 1290}]}}") ?: fail()

        assertEquals("base64encoded-image", item.message())
        assertFalse(item.error())
        assertTrue(item.image())
    }

}