package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.net.MalformedURLException
import java.net.URL
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.LinkDecoratorService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LinkDecoratorServiceTest {

    @Test
    fun test() {
        assertEquals("www.yahoo.co.jp", LinkDecoratorService().invoke("www.yahoo.co.jp"))
        mockkStatic(Jsoup::class)
        val document = mockk<Document>()
        every { Jsoup.parse(any<URL>(), any()) }.returns(document)
        every { document.title() } returns "Yahoo! JAPAN"
        assertEquals(
            "[Yahoo! JAPAN](https://www.yahoo.co.jp)",
            LinkDecoratorService().invoke("https://www.yahoo.co.jp")
        )
        clearAllMocks()
    }

    @Test
    fun testException() {
        mockkStatic(Jsoup::class)
        every { Jsoup.parse(any<URL>(), any()) }.throws(MalformedURLException())

        assertEquals("https://www.yahoo.co.jp", LinkDecoratorService().invoke("https://www.yahoo.co.jp"))
    }

}