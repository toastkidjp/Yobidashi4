package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.unit.sp
import io.mockk.MockKAnnotations
import io.mockk.Runs
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
import org.junit.jupiter.api.assertNotNull
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class CodeBlockViewModelTest {

    private lateinit var subject: CodeBlockViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single { mainViewModel }.bind(MainViewModel::class)
                }
            )
        }

        subject = CodeBlockViewModel()

        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
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
        every { mainViewModel.clipText(any()) } just Runs

        subject.clipContent()

        verify { mainViewModel.clipText(any()) }
    }

    @Test
    fun cursor() {
        assertEquals(0f, subject.alpha())
        subject.cursorOn()
        assertEquals(1f, subject.alpha())
        subject.cursorOff()
        assertEquals(0f, subject.alpha())
    }

}