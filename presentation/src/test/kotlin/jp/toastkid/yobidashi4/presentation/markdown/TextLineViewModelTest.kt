package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.text.AnnotatedString
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.find.FindOrder
import jp.toastkid.yobidashi4.presentation.editor.preview.LinkBehaviorService
import jp.toastkid.yobidashi4.presentation.editor.preview.LinkGenerator
import jp.toastkid.yobidashi4.presentation.lib.text.KeywordHighlighter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class TextLineViewModelTest {

    private lateinit var subject: TextLineViewModel

    @MockK
    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                }
            )
        }
        MockKAnnotations.init(this)
        every { viewModel.finderFlow() } returns flowOf(FindOrder.EMPTY)

        mockkConstructor(LinkGenerator::class, KeywordHighlighter::class, LinkBehaviorService::class)
        every { anyConstructed<LinkGenerator>().invoke(any()) } returns "generated"
        val annotatedString = mockk<AnnotatedString>()
        every { anyConstructed<KeywordHighlighter>().invoke(any(), any()) } returns annotatedString
        val element = mockk<AnnotatedString.Range<String>>()
        every { element.tag } returns "URL"
        every { element.item } returns "https://www.yahoo.com"
        every { annotatedString.getStringAnnotations(any(), any(), any()) } returns listOf(element)
        every { anyConstructed<LinkBehaviorService>().invoke(any()) } just Runs

        subject = TextLineViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun annotatedString() {
        assertTrue(subject.annotatedString().text.isEmpty())
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun onPointerReleased() {
        runBlocking {
            val pointerInputChange = mockk<PointerInputChange>()
            every { pointerInputChange.previousPressed } returns false
            every { pointerInputChange.pressed } returns true
            every { pointerInputChange.changedToDownIgnoreConsumed() } returns true
            val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
            every { pointerEvent.button } returns PointerButton.Secondary

            subject.launch("test")
            subject.onPointerReleased(PointerEvent(listOf(pointerInputChange)))

            verify { anyConstructed<LinkBehaviorService>().invoke(any()) }
            verify { anyConstructed<LinkGenerator>().invoke(any()) }
        }
    }

}