package jp.toastkid.yobidashi4.presentation.editor.usecase

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TextEditorOperationUseCaseTest {

    private lateinit var subject: TextEditorOperationUseCase

    @MockK
    private lateinit var mainViewModel: MainViewModel

    private val content = TextFieldState()

    @MockK
    private lateinit var multiParagraph: MultiParagraph

    @MockK
    private lateinit var multiParagraphState: () -> MultiParagraph?

    @MockK
    private lateinit var scrollBy: (Float) -> Unit

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { scrollBy.invoke(any()) } just Runs
        every { multiParagraphState.invoke() } returns multiParagraph
        content.clearText()

        subject = TextEditorOperationUseCase(
            mainViewModel, content, multiParagraphState, scrollBy, mockk()
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun scrollBy() {
        subject.scrollBy(1f)

        every { scrollBy.invoke(1f) }
    }

    @Test
    fun cutLine() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 4
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs
        every { multiParagraphState.invoke() } returns multiParagraph

        content.edit {
            append("test\ntest2\ntest3")
        }

        subject.cutLine()

        verify { scrollBy wasNot called }
        verify { multiParagraph.getLineForOffset(any()) }
        verify { multiParagraph.getLineStart(0) }
        verify { multiParagraph.getLineEnd(0) }
        verify { anyConstructed<ClipboardPutterService>().invoke("test\n") }
    }

    @Test
    fun cutLineOnSelectedTextCase() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 4
        every { multiParagraphState.invoke() } returns multiParagraph

        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs
        content.edit {
            append("test\ntest2\ntest3")
            selection = TextRange(1, 3)
        }

        subject.cutLine()

        verify { scrollBy wasNot called }
        verify { multiParagraph wasNot called }
        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun deleteLine() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 4
        every { multiParagraphState.invoke() } returns multiParagraph
        content.edit {
            append("test\ntest2\ntest3")
        }

        subject.deleteLine()

        verify { scrollBy wasNot called }
        verify { multiParagraph.getLineForOffset(any()) }
        verify { multiParagraph.getLineStart(0) }
        verify { multiParagraph.getLineEnd(0) }
        assertEquals("test2\ntest3", content.text)
    }

    @Test
    fun deleteLineOnSelectedText() {
        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 4
        every { multiParagraphState.invoke() } returns multiParagraph
        content.edit {
            append("test\ntest2\ntest3")
            selection = TextRange(0, 3)
        }

        subject.deleteLine()

        verify { scrollBy wasNot called }
        verify { multiParagraph wasNot called }
    }

    @Test
    fun deleteLineOnParagraphIsNull() {
        every { multiParagraphState.invoke() } returns null

        subject.deleteLine()
    }

    @Test
    fun hideFileList() {
        every { mainViewModel.switchArticleList() } just Runs
        every { mainViewModel.hideArticleList() } just Runs

        val consumed = subject.hideArticleList()

        verify(inverse = true) { mainViewModel.switchArticleList() }
        verify { mainViewModel.hideArticleList() }
    }

    @Test
    fun openFileList() {
        every { mainViewModel.openArticleList() } returns false
        every { mainViewModel.switchArticleList() } just Runs

        val consumed = subject.switchArticleList()

        verify { mainViewModel.openArticleList() }
        verify { mainViewModel.switchArticleList() }
        verify { scrollBy wasNot called }
    }

}