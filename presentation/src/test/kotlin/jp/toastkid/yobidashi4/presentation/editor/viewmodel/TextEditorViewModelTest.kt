package jp.toastkid.yobidashi4.presentation.editor.viewmodel

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
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

    @Test
    fun initialScroll() {
    }

    @Test
    fun lineNumbers() {
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
    }

    @Test
    fun makeCharacterCountMessage() {
        assertEquals("Character: 200", viewModel.makeCharacterCountMessage(200))
    }
}