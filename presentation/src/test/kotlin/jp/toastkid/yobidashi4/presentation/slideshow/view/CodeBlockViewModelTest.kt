package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.unit.sp
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

class CodeBlockViewModelTest {

    private lateinit var subject: CodeBlockViewModel

    @BeforeEach
    fun setUp() {
        subject = CodeBlockViewModel()

        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun maxHeight() {
        println(subject.maxHeight(16.sp))
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun verticalScrollState() {
        assertEquals(0, subject.verticalScrollState().value)
    }

    @Test
    fun horizontalScrollState() {
        assertEquals(0, subject.horizontalScrollState().value)
    }

    @Test
    fun lineNumberTexts() {
        val multiParagraph = mockk<MultiParagraph>()
        every { multiParagraph.lineCount } returns 14
        subject.setMultiParagraph(multiParagraph)

        assertEquals(14, subject.lineNumberTexts().size)
    }

    @Test
    fun outputTransformation() {
        assertNotNull(subject.outputTransformation())
    }

    @Test
    fun start() {
        subject.start("test")

        assertEquals("test", subject.content().text)
    }

    @Test
    fun clipContent() {
        subject.clipContent()

        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

}