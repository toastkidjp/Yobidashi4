package jp.toastkid.yobidashi4.presentation.tool.file

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class FileRenameToolViewModelTest {

    private lateinit var subject: FileRenameToolViewModel

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

        every { mainViewModel.droppedPathFlow() } returns emptyFlow()
        every { mainViewModel.showSnackbar(any(), any(), any()) } just Runs

        mockkStatic(Files::class)
        every { Files.copy(any<Path>(), any<Path>()) } returns mockk()

        subject = FileRenameToolViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun onValueChange() {
        assertEquals("img_", subject.input().text)

        subject.onValueChange(TextFieldValue("ABC"))

        assertEquals("ABC", subject.input().text)

        subject.clearInput()

        assertTrue(subject.input().text.isEmpty())
    }

    @Test
    fun rename() {
        subject.onValueChange(TextFieldValue("ABC"))
        val value = mockk<Path>()
        every { value.resolveSibling(any<String>()) } returns mockk()
        every { value.extension } returns "png"
        every { value.parent } returns value
        every { mainViewModel.droppedPathFlow() } returns flowOf(value, value)
        val slot = slot<() -> Unit>()
        every { mainViewModel.showSnackbar(any(), any(), capture(slot)) } just Runs
        every { mainViewModel.openFile(any()) } just Runs

        runBlocking {
            subject.collectDroppedPaths()

            subject.rename()

            verify(exactly = 2) { Files.copy(any<Path>(), any<Path>()) }
            verify(exactly = 1) { mainViewModel.showSnackbar(any(), any(), any()) }
            assertTrue(slot.isCaptured)

            slot.captured.invoke()
            verify { mainViewModel.openFile(any()) }
        }
    }

    @Test
    fun onKeyEvent() {
        subject.onValueChange(TextFieldValue("ABC"))

        val consumed = subject.onKeyEvent(
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_PRESSED,
                    1,
                    0,
                    java.awt.event.KeyEvent.VK_ENTER,
                    '-'
                )
            )
        )

        assertTrue(consumed)
        verify(inverse = true) { Files.copy(any<Path>(), any<Path>()) }
        verify(inverse = true) { mainViewModel.showSnackbar(any(), any(), any()) }
    }

    @Test
    fun onKeyEventNotConsumedWithKeyReleasing() {
        subject.onValueChange(TextFieldValue("ABC"))

        val consumed = subject.onKeyEvent(
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_RELEASED,
                    1,
                    0,
                    java.awt.event.KeyEvent.VK_ENTER,
                    '-'
                )
            )
        )

        assertFalse(consumed)
    }

    @Test
    fun onKeyEventNotConsumedWithOtherKey() {
        val consumed = subject.onKeyEvent(
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_PRESSED,
                    1,
                    0,
                    java.awt.event.KeyEvent.VK_0,
                    '0'
                )
            )
        )

        assertFalse(consumed)
    }

    @Test
    fun onKeyEventNotConsumedWithExistingComposition() {
        subject.onValueChange(TextFieldValue("ABC", composition = TextRange.Companion.Zero))

        val consumed = subject.onKeyEvent(
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_PRESSED,
                    1,
                    0,
                    java.awt.event.KeyEvent.VK_ENTER,
                    '-'
                )
            )
        )

        assertFalse(consumed)
    }

    @Test
    fun onKeyEventNotConsumedWithTextIsEmpty() {
        subject.onValueChange(TextFieldValue())

        val consumed = subject.onKeyEvent(
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_PRESSED,
                    1,
                    0,
                    java.awt.event.KeyEvent.VK_ENTER,
                    '-'
                )
            )
        )

        assertFalse(consumed)
    }

    @Test
    fun collectDroppedPaths() {
        runBlocking {
            assertTrue(subject.items().isEmpty())

            every { mainViewModel.droppedPathFlow() } returns flowOf(mockk())

            subject.collectDroppedPaths()

            assertEquals(1, subject.items().size)

            subject.clearPaths()

            assertTrue(subject.items().isEmpty())
        }
    }

}