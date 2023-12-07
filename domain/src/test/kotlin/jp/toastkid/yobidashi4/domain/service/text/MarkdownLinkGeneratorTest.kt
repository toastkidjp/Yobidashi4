package jp.toastkid.yobidashi4.domain.service.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MarkdownLinkGeneratorTest {

    private lateinit var subject: MarkdownLinkGenerator

    @BeforeEach
    fun setUp() {
        subject = MarkdownLinkGenerator()
    }

    @Test
    fun invoke() {
        assertEquals("[Yahoo! JAPAN](https://www.yahoo.co.jp)", subject.invoke("Yahoo! JAPAN", "https://www.yahoo.co.jp"))
        assertEquals("[https://www.yahoo.co.jp](https://www.yahoo.co.jp)", subject.invoke(null, "https://www.yahoo.co.jp"))
        assertEquals("[Yahoo! JAPAN](null)", subject.invoke("Yahoo! JAPAN", null))
        assertEquals("[null](null)", subject.invoke(null, null))
    }

}