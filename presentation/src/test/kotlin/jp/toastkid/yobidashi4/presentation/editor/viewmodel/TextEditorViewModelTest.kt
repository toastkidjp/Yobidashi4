package jp.toastkid.yobidashi4.presentation.editor.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class TextEditorViewModelTest {

    private lateinit var viewModel: TextEditorViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var multiParagraph: MultiParagraph

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }
        viewModel = TextEditorViewModel()

        every { mainViewModel.updateEditorContent(any(), any(), any(), any(), any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun content() {
    }

    @Test
    fun onValueChange() {
    }

    @Test
    fun setMultiParagraph() {
        assertEquals(Offset.Unspecified, viewModel.currentLineOffset())

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineLeft(any()) } returns 20f
        every { multiParagraph.getLineTop(any()) } returns 0f
        every { multiParagraph.lineCount } returns 3

        viewModel.setMultiParagraph(multiParagraph)

        val currentLineOffset = viewModel.currentLineOffset()
        assertEquals(20f, currentLineOffset.x)
        assertEquals(0f, currentLineOffset.y)
    }

    @Test
    fun verticalScrollState() {
    }

    @Test
    fun scrollbarAdapter() {
    }

    @Test
    fun lineNumberScrollState() {
    }

    @Test
    fun onClickLineNumber() {
    }

    @Test
    fun focusRequester() {
    }

    @Test
    fun onKeyEvent() {
    }

    @Test
    fun adjustLineNumberState() {
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun initialScroll() {
        viewModel.initialScroll(CoroutineScope(Dispatchers.Unconfined), 0L)

        assertEquals(0.0f, viewModel.verticalScrollState().offset)
    }

    @Test
    fun lineNumbers() {
        assertTrue(viewModel.lineNumbers().isEmpty())
    }

    @Test
    fun launchTab() {
    }

    @Test
    fun dispose() {
        viewModel.onValueChange(TextFieldValue("test"))

        viewModel.dispose()

        verify { mainViewModel.updateEditorContent(any(), any(), any(), any(), any()) }
        assertTrue(viewModel.content().text.isEmpty())
    }

    @Test
    fun disposeWithEmptyContent() {
        viewModel.dispose()

        verify(inverse = true) { mainViewModel.updateEditorContent(any(), any(), any(), any(), any()) }
        assertTrue(viewModel.content().text.isEmpty())
    }

    @Test
    fun currentLineHighlightColor() {
        every { mainViewModel.darkMode() } returns false
        val lineHighlightColorOnLight = viewModel.currentLineHighlightColor()
        every { mainViewModel.darkMode() } returns true
        val lineHighlightColorOnDark = viewModel.currentLineHighlightColor()

        assertNotEquals(lineHighlightColorOnLight, lineHighlightColorOnDark)
    }

    @Test
    fun visualTransformation() {
        assertNotEquals(VisualTransformation.None, viewModel.visualTransformation())
    }

    @Test
    fun makeCharacterCountMessage() {
        assertEquals("Character: 200", viewModel.makeCharacterCountMessage(200))
    }
}