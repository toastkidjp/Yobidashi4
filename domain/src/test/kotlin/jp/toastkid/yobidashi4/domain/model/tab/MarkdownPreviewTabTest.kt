package jp.toastkid.yobidashi4.domain.model.tab

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.markdown.Markdown
import jp.toastkid.yobidashi4.domain.service.markdown.MarkdownParser
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MarkdownPreviewTabTest {

    private lateinit var tab: MarkdownPreviewTab

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var markdown: Markdown

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkConstructor(MarkdownParser::class)
        every { anyConstructed<MarkdownParser>().invoke(any()) } returns markdown
        every { markdown.title() } returns "test.md"

        tab = MarkdownPreviewTab.with(path)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun title() {
        assertEquals("test.md", tab.title())
        verify { markdown.title() }
    }

    @Test
    fun iconPath() {
        assertTrue(tab.iconPath()?.startsWith("images/icon/") ?: false)
    }

    @Test
    fun withNewPosition() {
        val withNewPosition = tab.withNewPosition(20)

        assertNotSame(withNewPosition, tab)
        assertEquals(20, withNewPosition.scrollPosition())
    }

    @Test
    fun markdown() {
        assertSame(markdown, tab.markdown())
    }

    @Test
    fun slideshowSourcePath() {
        assertSame(path, tab.slideshowSourcePath())
    }

}