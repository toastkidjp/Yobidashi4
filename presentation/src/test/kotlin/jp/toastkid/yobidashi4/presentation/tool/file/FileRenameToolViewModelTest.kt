package jp.toastkid.yobidashi4.presentation.tool.file

import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.service.tool.file.FileRenamer
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
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

    @MockK
    private lateinit var fileRenamer: FileRenamer

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier = null) { fileRenamer } bind(FileRenamer::class)
                }
            )
        }

        every { mainViewModel.showSnackbar(any(), any(), any()) } just Runs

        subject = FileRenameToolViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun listState() {
        assertEquals(0, subject.listState().firstVisibleItemIndex)
    }

    @Test
    fun onValueChange() {
        assertEquals("img", subject.input().text)

        subject.input().setTextAndPlaceCursorAtEnd("ABC")

        assertEquals("ABC", subject.input().text)

        subject.clearInput()

        assertTrue(subject.input().text.isEmpty())
    }

    @Test
    fun rename() {
        val fileRenamerSlot = slot<() -> Unit>()
        every { fileRenamer.invoke(any(), any(), any(), capture(fileRenamerSlot)) } just Runs
        val slot = slot<() -> Unit>()
        every { mainViewModel.showSnackbar(any(), any(), capture(slot)) } just Runs
        every { mainViewModel.openFile(any()) } just Runs

        subject.rename()

        verify { fileRenamer.invoke(any(), any(), any(), any()) }
        fileRenamerSlot.captured.invoke()

        verify(exactly = 1) { mainViewModel.showSnackbar(any(), any(), any()) }
        assertTrue(slot.isCaptured)
        slot.captured.invoke()
        verify(inverse = true) { mainViewModel.openFile(any()) }
    }

    @Test
    fun renameIfFilesAreEmptyCase() {
        val fileRenamerSlot = slot<() -> Unit>()
        every { fileRenamer.invoke(any(), any(), any(), capture(fileRenamerSlot)) } just Runs
        val slot = slot<() -> Unit>()
        every { mainViewModel.showSnackbar(any(), any(), capture(slot)) } just Runs
        every { mainViewModel.openFile(any()) } just Runs
        subject = spyk(subject)
        val element = kotlin.io.path.createTempFile()
        every { subject.items() } returns listOf(element)

        subject.rename()

        verify { fileRenamer.invoke(any(), any(), any(), any()) }
        fileRenamerSlot.captured.invoke()

        verify(exactly = 1) { mainViewModel.showSnackbar(any(), any(), any()) }
        assertTrue(slot.isCaptured)
        slot.captured.invoke()
        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun renamedSampleFileName() {
        assertEquals("img_1.png", subject.renamedSampleFileName())
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEvent() {
        subject.input().setTextAndPlaceCursorAtEnd("ABC")

        val consumed = subject.onKeyEvent(KeyEvent(Key.Enter, KeyEventType.KeyDown))

        assertTrue(consumed)
        verify(inverse = true) { mainViewModel.showSnackbar(any(), any(), any()) }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventNotConsumedWithKeyReleasing() {
        subject.input().setTextAndPlaceCursorAtEnd("ABC")

        val consumed = subject.onKeyEvent(KeyEvent(Key.Enter, KeyEventType.KeyUp))

        assertFalse(consumed)
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventNotConsumedWithOtherKey() {
        val consumed = subject.onKeyEvent(KeyEvent(Key.Zero, KeyEventType.KeyDown))

        assertFalse(consumed)
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventNotConsumedWithExistingComposition() {
        subject.input().setTextAndPlaceCursorAtEnd("ABC")
            // TODO composition = TextRange.Companion.Zero

        val consumed = subject.onKeyEvent(KeyEvent(Key.Enter, KeyEventType.KeyDown))

        // TODO assertFalse(consumed)
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventNotConsumedWithTextIsEmpty() {
        subject.input().clearText()

        val consumed = subject.onKeyEvent(KeyEvent(Key.Enter, KeyEventType.KeyDown))

        assertFalse(consumed)
    }

    @Test
    fun dispose() {
        every { mainViewModel.unregisterDroppedPathReceiver() } just Runs

        subject.dispose()

        verify { mainViewModel.unregisterDroppedPathReceiver() }
    }

    @Test
    fun remove() {
        subject.remove(mockk())
    }

    @Test
    fun useResize() {
        assertFalse(subject.useResize().value)

        subject.switchUseResize()

        assertTrue(subject.useResize().value)
    }

}