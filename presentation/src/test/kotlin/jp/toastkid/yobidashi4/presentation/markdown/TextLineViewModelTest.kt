package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.material.Text
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
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
        every { viewModel.putSecondaryClickItem(any()) } just Runs

        mockkConstructor(LinkGenerator::class, KeywordHighlighter::class, LinkBehaviorService::class)
        every { anyConstructed<LinkGenerator>().invoke(any()) } returns "generated"
        every { anyConstructed<LinkBehaviorService>().invoke(any(), any()) } just Runs

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

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalTestApi::class)
    @Test
    fun onPointerReleased() {
        val annotatedString = buildAnnotatedString {
            append("test https://www.yahoo.com")
            addStringAnnotation("URL", "", 0, 10)
        }
        every { anyConstructed<KeywordHighlighter>().invoke(any(), any()) } returns annotatedString

        var textLayoutResult: TextLayoutResult? = null
        runDesktopComposeUiTest {
            setContent {
                Text(annotatedString, onTextLayout = {
                    textLayoutResult = it
                })
            }
        }

        subject = TextLineViewModel()

        runBlocking {
            val pointerInputChange = PointerInputChange(
                id = PointerId(1),
                uptimeMillis = 0,
                position = Offset.Zero,
                pressed = false,
                previousUptimeMillis = 1,
                previousPosition = Offset.Zero,
                previousPressed = false,
                isInitiallyConsumed = false,
                type = PointerType.Touch,
                scrollDelta = Offset.Zero
            )
            val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
            every { pointerEvent.button } returns PointerButton.Primary

            subject.launch("test")
            textLayoutResult?.let {
                subject.putLayoutResult(it)
            }

            subject.onPointerReleased(pointerEvent)

            verify { anyConstructed<LinkBehaviorService>().invoke(any(), any()) }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalTestApi::class)
    @Test
    fun noopOnPointerReleasedIfAnnotationIsNotUrl() {
        val annotatedString = buildAnnotatedString {
            append("test https://www.yahoo.com")
            addStringAnnotation("Other", "", 0, 10)
        }
        every { anyConstructed<KeywordHighlighter>().invoke(any(), any()) } returns annotatedString

        var textLayoutResult: TextLayoutResult? = null
        runDesktopComposeUiTest {
            setContent {
                Text(annotatedString, onTextLayout = {
                    textLayoutResult = it
                })
            }
        }

        subject = TextLineViewModel()

        runBlocking {
            val pointerInputChange = PointerInputChange(
                id = PointerId(1),
                uptimeMillis = 0,
                position = Offset.Zero,
                pressed = false,
                previousUptimeMillis = 1,
                previousPosition = Offset.Zero,
                previousPressed = false,
                isInitiallyConsumed = false,
                type = PointerType.Touch,
                scrollDelta = Offset.Zero
            )
            val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
            every { pointerEvent.button } returns PointerButton.Secondary

            subject.launch("test")
            textLayoutResult?.let {
                subject.putLayoutResult(it)
            }
            subject.onPointerReleased(PointerEvent(listOf(pointerInputChange)))

            verify(inverse = true) { anyConstructed<LinkBehaviorService>().invoke(any(), any()) }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalTestApi::class)
    @Test
    fun noopOnPointerReleasedWhenUrlIsNotFound() {
        val annotatedString = AnnotatedString("test https://www.yahoo.com")
        every { anyConstructed<KeywordHighlighter>().invoke(any(), any()) } returns annotatedString

        var textLayoutResult: TextLayoutResult? = null
        runDesktopComposeUiTest {
            setContent {
                Text(annotatedString, onTextLayout = {
                    textLayoutResult = it
                })
            }
        }

        subject = TextLineViewModel()

        runBlocking {
            val pointerInputChange = PointerInputChange(
                id = PointerId(1),
                uptimeMillis = 0,
                position = Offset.Zero,
                pressed = false,
                previousUptimeMillis = 1,
                previousPosition = Offset.Zero,
                previousPressed = false,
                isInitiallyConsumed = false,
                type = PointerType.Touch,
                scrollDelta = Offset.Zero
            )
            val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
            every { pointerEvent.button } returns PointerButton.Secondary

            subject.launch("test")
            textLayoutResult?.let {
                subject.putLayoutResult(it)
            }
            subject.onPointerReleased(PointerEvent(listOf(pointerInputChange)))

            verify(inverse = true) { anyConstructed<LinkBehaviorService>().invoke(any(), any()) }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun noopOnPointerReleased() {
        runBlocking {
            subject.onPointerReleased(PointerEvent(emptyList()))

            verify(inverse = true) { anyConstructed<LinkBehaviorService>().invoke(any(), any()) }
            verify(inverse = true) { anyConstructed<LinkGenerator>().invoke(any()) }
        }
    }

}