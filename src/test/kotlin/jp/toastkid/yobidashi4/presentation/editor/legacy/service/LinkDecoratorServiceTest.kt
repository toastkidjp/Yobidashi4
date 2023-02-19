package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import io.mockk.every
import io.mockk.mockkStatic
import java.net.MalformedURLException
import java.net.URL
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LinkDecoratorServiceTest {

    @Test
    fun test() {
        assertEquals("www.yahoo.co.jp", LinkDecoratorService().invoke("www.yahoo.co.jp"))
        assertEquals("[Yahoo! JAPAN](https://www.yahoo.co.jp)", LinkDecoratorService().invoke("https://www.yahoo.co.jp"))
    }

    @Test
    fun testException() {
        mockkStatic(Jsoup::class)
        every { Jsoup.parse(any<URL>(), any()) }.throws(MalformedURLException())

        assertEquals("https://www.yahoo.co.jp", LinkDecoratorService().invoke("https://www.yahoo.co.jp"))
    }

}