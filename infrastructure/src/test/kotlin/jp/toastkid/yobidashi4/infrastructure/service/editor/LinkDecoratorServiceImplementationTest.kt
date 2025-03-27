package jp.toastkid.yobidashi4.infrastructure.service.editor

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.net.URL

internal class LinkDecoratorServiceImplementationTest {

    @BeforeEach
    fun setUp() {
        mockkStatic(Jsoup::class)
        val document = mockk<Document>()
        every { Jsoup.parse(any<URL>(), any()) }.returns(document)
        every { document.title() } returns "Yahoo! JAPAN"
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test() {
        assertEquals("www.yahoo.co.jp", LinkDecoratorServiceImplementation().invoke("www.yahoo.co.jp"))
        assertEquals(
            "[Yahoo! JAPAN](https://www.yahoo.co.jp)",
            LinkDecoratorServiceImplementation().invoke("https://www.yahoo.co.jp")
        )
    }

    @Test
    fun testException() {
        every { Jsoup.parse(any<URL>(), any()) }.throws(IOException())

        assertEquals("https://www.yahoo.co.jp", LinkDecoratorServiceImplementation().invoke("https://www.yahoo.co.jp"))
    }

    @Test
    fun trim() {
        LinkDecoratorServiceImplementation()
            .invoke("https://ja.wikipedia.org/wiki/MSCI#%E9%81%8E%E5%8E%BB%E3%81%AE%E5%88%A9%E5%9B%9E%E3%82%8A\n")

        verify { Jsoup.parse(any<URL>(), any()) }
    }

}
