package jp.toastkid.yobidashi4.presentation.editor.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.text.input.VisualTransformation
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
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

        assertEquals(0.0, viewModel.verticalScrollState().offset)
    }

    @Test
    fun lineNumbers() {
        assertTrue(viewModel.lineNumbers().isEmpty())
    }

    @Test
    fun launchTab() {
    }

    @Test
    fun currentLineOffset() {
    }

    @Test
    fun dispose() {
    }

    @Test
    fun currentLineHighlightColor() {
    }

    @Test
    fun visualTransformation() {
        assertEquals(VisualTransformation.None, viewModel.visualTransformation())
    }

    @Test
    fun makeCharacterCountMessage() {
        assertEquals("Character: 200", viewModel.makeCharacterCountMessage(200))
    }
}