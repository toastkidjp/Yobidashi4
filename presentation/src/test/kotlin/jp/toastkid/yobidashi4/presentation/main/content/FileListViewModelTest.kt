package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
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
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.event.KeyEvent
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.service.archive.KeywordArticleFinder
import jp.toastkid.yobidashi4.domain.service.archive.ZipArchiver
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
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

    @MockK
    private lateinit var keywordSearch: KeywordArticleFinder

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

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

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun onKeyEvent() {
        runDesktopComposeUiTest {
            setContent {
                mockkConstructor(ZipArchiver::class)
                every { anyConstructed<ZipArchiver>().invoke(any()) } just Runs

                val consumed = subject.onKeyEvent(
                    rememberCoroutineScope(),
                    androidx.compose.ui.input.key.KeyEvent(
                        KeyEvent(
                            mockk(),
                            KeyEvent.KEY_PRESSED,
                            1,
                            KeyEvent.CTRL_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK,
                            KeyEvent.VK_Z,
                            'Z'
                        )
                    )
                )

                assertTrue(consumed)
                verify { anyConstructed<ZipArchiver>().invoke(any()) }
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun onDirectionUpKeyEvent() {
        runDesktopComposeUiTest {
            setContent {
                val consumed = subject.onKeyEvent(
                    rememberCoroutineScope(),
                    androidx.compose.ui.input.key.KeyEvent(
                        KeyEvent(
                            mockk(),
                            KeyEvent.KEY_PRESSED,
                            1,
                            -1,
                            KeyEvent.VK_UP,
                            '↑'
                        )
                    )
                )

                assertTrue(consumed)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun unConsumedOnKeyEvent() {
        runDesktopComposeUiTest {
            setContent {
                mockkConstructor(ZipArchiver::class)
                every { anyConstructed<ZipArchiver>().invoke(any()) } just Runs

                val consumed = subject.onKeyEvent(
                    rememberCoroutineScope(),
                    androidx.compose.ui.input.key.KeyEvent(
                        KeyEvent(
                            mockk(),
                            KeyEvent.KEY_PRESSED,
                            1,
                            KeyEvent.CTRL_DOWN_MASK or KeyEvent.SHIFT_DOWN_MASK,
                            KeyEvent.VK_Q,
                            'Q'
                        )
                    )
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

}