package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.service.archive.ZipArchiver
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItem
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension
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

class FileListViewModelTest {

    private lateinit var subject: FileListViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                }
            )
        }

        every { mainViewModel.openFile(any()) } just Runs
        every { mainViewModel.edit(any(), any()) } just Runs
        every { mainViewModel.hideArticleList() } just Runs

        subject = FileListViewModel()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun horizontalScrollState() {
        assertEquals(0, subject.horizontalScrollState().value)
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEvent() {
        runDesktopComposeUiTest {
            setContent {
                mockkConstructor(ZipArchiver::class)
                every { anyConstructed<ZipArchiver>().invoke(any()) } just Runs

                val consumed = subject.onKeyEvent(
                    rememberCoroutineScope(),
                    androidx.compose.ui.input.key.KeyEvent(
                        Key.Z, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true
                    )
                )

                assertTrue(consumed)
                verify { anyConstructed<ZipArchiver>().invoke(any()) }
            }
        }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventFromCell() {
        val path = mockk<Path>()
        subject = spyk(subject)
        every { subject.edit(any()) } just Runs
        every { mainViewModel.hideArticleList() } just Runs

        val consumed = subject.onKeyEventFromCell(
            androidx.compose.ui.input.key.KeyEvent(Key.Enter, KeyEventType.KeyDown),
            path
        )

        assertTrue(consumed)
        verify { subject.edit(any()) }
        verify { mainViewModel.hideArticleList() }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventFromCellUnconsumed() {
        val consumed = subject.onKeyEventFromCell(
            androidx.compose.ui.input.key.KeyEvent(Key.One, KeyEventType.KeyDown),
            mockk()
        )

        assertFalse(consumed)
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onDirectionUpKeyEvent() {
        runDesktopComposeUiTest {
            setContent {
                val consumed = subject.onKeyEvent(
                    rememberCoroutineScope(),
                    androidx.compose.ui.input.key.KeyEvent(Key.DirectionUp, KeyEventType.KeyDown)
                )

                assertTrue(consumed)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onDirectionDownKeyEvent() {
        runDesktopComposeUiTest {
            setContent {
                val consumed = subject.onKeyEvent(
                    rememberCoroutineScope(),
                    androidx.compose.ui.input.key.KeyEvent(Key.DirectionDown, KeyEventType.KeyDown)
                )

                assertTrue(consumed)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun unConsumedOnKeyEvent() {
        runDesktopComposeUiTest {
            setContent {
                mockkConstructor(ZipArchiver::class)
                every { anyConstructed<ZipArchiver>().invoke(any()) } just Runs

                val consumed = subject.onKeyEvent(
                    rememberCoroutineScope(),
                    androidx.compose.ui.input.key.KeyEvent(Key.Q, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true)
                )

                assertFalse(consumed)
                verify(inverse = true) { anyConstructed<ZipArchiver>().invoke(any()) }
            }
        }
    }

    @Test
    fun currentIsTop() {
        assertTrue(subject.currentIsTop())
    }

    @Test
    fun onValueChange() {
        subject.start(
            listOf(
                mockk<Path>().also {
                    every { it.extension } returns "md"
                    every { it.nameWithoutExtension } returns "TEST.md"
                },
                mockk<Path>().also {
                    every { it.extension } returns "md"
                    every { it.nameWithoutExtension } returns "GUeST.md"
                }
            )
        )

        subject.onValueChange(TextFieldValue("test"))
        assertEquals(1, subject.items().size)
    }

    @Test
    fun onValueChangeWithEmpty() {
        subject.start(
            listOf(
                mockk<Path>().also {
                    every { it.extension } returns "md"
                    every { it.nameWithoutExtension } returns "TEST.md"
                },
                mockk<Path>().also {
                    every { it.extension } returns "md"
                    every { it.nameWithoutExtension } returns "GUeST.md"
                }
            )
        )

        subject.onValueChange(TextFieldValue())
        assertEquals(2, subject.items().size)
    }

    @Test
    fun onValueChangeWithComposition() {
        subject.onValueChange(TextFieldValue("test", composition = TextRange.Zero))

        subject.clearInput()

        assertTrue(subject.keyword().text.isEmpty())
    }

    @Test
    fun onSingleClick() {
        subject.start(
            listOf(
                mockk<Path>().also { every { it.extension } returns "md" },
                mockk<Path>().also { every { it.extension } returns "txt" },
                mockk<Path>().also { every { it.extension } returns "exe" },
                mockk<Path>().also { every { it.extension } returns "html" }
            )
        )

        subject.onSingleClick(subject.items().first())
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onSingleClickWithShift() {
        subject.onKeyEvent(
            mockk(),
            androidx.compose.ui.input.key.KeyEvent(Key.Q, KeyEventType.KeyDown, isShiftPressed = true)
        )

        subject.start(
            listOf(
                mockk<Path>().also { every { it.extension } returns "md" },
                mockk<Path>().also { every { it.extension } returns "txt" },
                mockk<Path>().also { every { it.extension } returns "exe" },
                mockk<Path>().also { every { it.extension } returns "html" }
            )
        )

        subject.onSingleClick(subject.items().first())
    }

    @Test
    fun onLongClick() {
        val path = mockk<Path>()
        every { path.fileName.toString() } returns "test.md"
        subject.start(listOf(path))

        val fileListItem = subject.items().first()
        println(fileListItem)
        subject.onLongClick(fileListItem)

        verify(inverse = true) { mainViewModel.openFile(any()) }
        verify { mainViewModel.edit(any(), any()) }
    }

    @Test
    fun onLongClickUnEditableItem() {
        val path = mockk<Path>()
        every { path.fileName.toString() } returns "test.exe"
        subject.start(listOf(path))

        subject.onLongClick(subject.items().first())

        verify { mainViewModel.openFile(any()) }
        verify(inverse = true) { mainViewModel.edit(any(), any()) }
    }

    @Test
    fun onDoubleClick() {
        val path = mockk<Path>()
        every { path.fileName.toString() } returns "test.md"
        subject.start(listOf(path))

        subject.onDoubleClick(subject.items().first())

        verify(inverse = true) { mainViewModel.openFile(any()) }
        verify { mainViewModel.hideArticleList() }
        verify { mainViewModel.edit(any(), any()) }
    }

    @Test
    fun onDoubleClickUnEditableItem() {
        val path = mockk<Path>()
        every { path.fileName.toString() } returns "test.exe"
        subject.start(listOf(path))

        subject.onDoubleClick(subject.items().first())

        verify { mainViewModel.openFile(any()) }
        verify(inverse = true) { mainViewModel.hideArticleList() }
        verify(inverse = true) { mainViewModel.edit(any(), any()) }
    }

    @Test
    fun dropdown() {
        assertFalse(subject.openingDropdown(mockk()))

        val item = mockk<FileListItem>()
        subject.openDropdown(item)

        assertFalse(subject.openingDropdown(mockk()))
        assertTrue(subject.openingDropdown(item))

        subject.closeDropdown()

        assertFalse(subject.openingDropdown(mockk()))
        assertFalse(subject.openingDropdown(item))
    }

    @Test
    fun focus() {
        assertFalse(subject.focusingItem(mockk()))

        val item = mockk<FileListItem>()
        subject.focusItem(item)

        assertFalse(subject.focusingItem(mockk()))
        assertTrue(subject.focusingItem(item))

        subject.unFocusItem()

        assertFalse(subject.focusingItem(mockk()))
        assertFalse(subject.focusingItem(item))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun onPointerEvent() {
        val bookmark = mockk<FileListItem>()
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        every { pointerInputChange.changedToDownIgnoreConsumed() } returns true
        val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
        every { pointerEvent.button } returns PointerButton.Secondary

        subject.onPointerEvent(pointerEvent, bookmark)

        assertTrue(subject.openingDropdown(bookmark))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun noopOnPointerEvent() {
        val bookmark = mockk<FileListItem>()
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        every { pointerInputChange.changedToDownIgnoreConsumed() } returns false
        val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
        every { pointerEvent.button } returns PointerButton.Secondary

        subject.onPointerEvent(pointerEvent, bookmark)

        assertFalse(subject.openingDropdown(bookmark))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun noopOnPointerEventOnOpeningDropdown() {
        val bookmark = mockk<FileListItem>()
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        every { pointerInputChange.changedToDownIgnoreConsumed() } returns true
        val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
        every { pointerEvent.button } returns PointerButton.Secondary
        subject.openDropdown(bookmark)

        subject.onPointerEvent(pointerEvent, bookmark)

        assertTrue(subject.openingDropdown(bookmark))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun noopOnPointerEventWithOtherButton() {
        val bookmark = mockk<FileListItem>()
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        every { pointerInputChange.changedToDownIgnoreConsumed() } returns true
        val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
        every { pointerEvent.button } returns PointerButton.Primary

        subject.onPointerEvent(pointerEvent, bookmark)

        assertFalse(subject.openingDropdown(bookmark))
    }

    @Test
    fun openFile() {
        every { mainViewModel.openFile(any()) } just Runs

        subject.openFile(mockk())

        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun openFileWithDefaultParameter() {
        every { mainViewModel.openFile(any()) } just Runs

        subject.openFile()

        verify { mainViewModel wasNot called }
    }

    @Test
    fun edit() {
        every { mainViewModel.edit(any()) } just Runs

        subject.edit(mockk())

        verify { mainViewModel.edit(any()) }
    }

    @Test
    fun preview() {
        every { mainViewModel.openPreview(any()) } just Runs

        subject.preview(mockk())

        verify { mainViewModel.openPreview(any()) }
    }

    @Test
    fun slideshow() {
        every { mainViewModel.slideshow(any()) } just Runs

        subject.slideshow(mockk())

        verify { mainViewModel.slideshow(any()) }
    }

    @Test
    fun clipText() {
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        subject.clipText("test")

        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

}