/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.text.input.clearText
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.test.ExperimentalTestApi
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.service.archive.ZipArchiver
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItem
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItemFactory
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItemMeta
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItemMetaExtractor
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
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension

class FileListViewModelTest {

    private lateinit var subject: FileListViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var fileListItemMetaExtractor: FileListItemMetaExtractor

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                    single(qualifier = null) { fileListItemMetaExtractor } bind (FileListItemMetaExtractor::class)
                }
            )
        }

        every { mainViewModel.openFile(any()) } just Runs
        every { mainViewModel.edit(any(), any()) } just Runs
        every { mainViewModel.hideArticleList() } just Runs
        every { fileListItemMetaExtractor.make(any()) } returns FileListItemMeta("test", 0L)

        mockkStatic(Files::class)
        every { Files.getLastModifiedTime(any()) } answers { FileTime.fromMillis(System.currentTimeMillis()) }
        every { Files.exists(any()) } returns true
        every { Files.size(any()) } returns 10000

        mockkConstructor(FileListItemFactory::class)
        every { anyConstructed<FileListItemFactory>().invoke(any(), any()) } returns mockk(relaxed = true)

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
    fun onKeyEventWithKeyUp() {
        mockkConstructor(ZipArchiver::class)
        every { anyConstructed<ZipArchiver>().invoke(any()) } just Runs

        val consumed = subject.onKeyEvent(
            androidx.compose.ui.input.key.KeyEvent(
                Key.Z, KeyEventType.KeyUp, isCtrlPressed = true, isShiftPressed = true
            )
        )

        assertFalse(consumed)
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onKeyEvent() {
        mockkConstructor(ZipArchiver::class)
        every { anyConstructed<ZipArchiver>().invoke(any()) } just Runs

        val consumed = subject.onKeyEvent(
            androidx.compose.ui.input.key.KeyEvent(
                Key.Z, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true
            )
        )

        assertTrue(consumed)
        verify { anyConstructed<ZipArchiver>().invoke(any()) }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventFromCell() {
        val path = mockk<Path>()
        val fileListItem = mockk<FileListItem>()
        every { fileListItem.editable } returns true
        every { fileListItem.path } returns path
        subject = spyk(subject)
        every { subject.edit(any()) } just Runs
        every { mainViewModel.hideArticleList() } just Runs

        val consumed = subject.onKeyEventFromCell(
            androidx.compose.ui.input.key.KeyEvent(Key.Enter, KeyEventType.KeyDown),
            fileListItem
        )

        assertTrue(consumed)
        verify { subject.edit(any()) }
        verify { mainViewModel.hideArticleList() }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onKeyEventFromCellWithNotEditableItem() {
        val path = mockk<Path>()
        val fileListItem = mockk<FileListItem>()
        every { fileListItem.editable } returns false
        every { fileListItem.path } returns path
        subject = spyk(subject)
        every { subject.edit(any()) } just Runs
        every { mainViewModel.openFile(any()) } just Runs
        every { mainViewModel.hideArticleList() } just Runs

        val consumed = subject.onKeyEventFromCell(
            androidx.compose.ui.input.key.KeyEvent(Key.Enter, KeyEventType.KeyDown),
            fileListItem
        )

        assertTrue(consumed)
        verify { mainViewModel.openFile(any()) }
        verify(inverse = true) { subject.edit(any()) }
        verify(inverse = true) { mainViewModel.hideArticleList() }
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
        val consumed = subject.onKeyEvent(
            androidx.compose.ui.input.key.KeyEvent(Key.DirectionUp, KeyEventType.KeyDown)
        )

        assertTrue(consumed)
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun onDirectionDownKeyEvent() {
        val consumed = subject.onKeyEvent(
            androidx.compose.ui.input.key.KeyEvent(Key.DirectionDown, KeyEventType.KeyDown)
        )

        assertTrue(consumed)
    }

    @OptIn(ExperimentalTestApi::class, InternalComposeUiApi::class)
    @Test
    fun unConsumedOnKeyEvent() {
        mockkConstructor(ZipArchiver::class)
        every { anyConstructed<ZipArchiver>().invoke(any()) } just Runs

        val consumed = subject.onKeyEvent(
            androidx.compose.ui.input.key.KeyEvent(Key.Q, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true)
        )

        assertFalse(consumed)
        verify(inverse = true) { anyConstructed<ZipArchiver>().invoke(any()) }
    }

    @Test
    fun currentIsTop() {
        assertTrue(subject.currentIsTop())
    }

    @Test
    fun onValueChange() {
        val path1 = mockk<Path>()
        val path2 = mockk<Path>()
        every { anyConstructed<FileListItemFactory>().invoke(any(), any()) } answers {
            val path = arg<Path>(0)
            val fileListItem = mockk<FileListItem>(relaxed = true)
            every { fileListItem.keep() } returns true
            every { fileListItem.name() } answers {
                if (path1 === path) "TEST.md" else "GUeST.md"
            }
            fileListItem
        }
        subject.start(
            listOf(
                path1,
                path2
            )
        )
        subject.keyword().clearText()
        subject.keyword().edit {
            append("TEST")
        }

        subject.onValueChange()
        assertEquals(1, subject.items().size)
        subject.keyword().clearText()
    }

    @Test
    fun onValueChangeWithEmpty() {
        val path1 = mockk<Path>()
        val path2 = mockk<Path>()
        every { anyConstructed<FileListItemFactory>().invoke(any(), any()) } answers {
            val path = arg<Path>(0)
            val fileListItem = mockk<FileListItem>(relaxed = true)
            every { fileListItem.keep() } returns true
            every { fileListItem.name() } answers {
                if (path1 === path) "TEST.md" else "GUeST.md"
            }
            fileListItem
        }
        subject.start(
            listOf(
                path1,
                path2
            )
        )

        subject.onValueChange()
        assertEquals(2, subject.items().size)
    }

    @Test
    fun onValueChangeWithComposition() {
        subject.onValueChange()

        subject.clearInput()

        assertTrue(subject.keyword().text.isEmpty())
    }

    @Test
    fun onSingleClick() {
        subject.start(
            listOf(
                mockk<Path>(),
                mockk<Path>(),
                mockk<Path>(),
                mockk<Path>()
            )
        )

        subject.onSingleClick(subject.items().first())
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onSingleClickWithShift() {
        subject.onKeyEvent(
            androidx.compose.ui.input.key.KeyEvent(Key.Q, KeyEventType.KeyDown, isShiftPressed = true)
        )

        subject.start(
            listOf(
                mockk<Path>(),
                mockk<Path>(),
                mockk<Path>(),
                mockk<Path>()
            )
        )

        subject.onSingleClick(subject.items().first())
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onSingleClickWithShiftAndLastClicked() {
        subject.start(
            listOf(
                mockk<Path>(),
                mockk<Path>(),
                mockk<Path>(),
                mockk<Path>()
            )
        )
        subject.onSingleClick(subject.items().last())
        subject.onKeyEvent(
            androidx.compose.ui.input.key.KeyEvent(Key.Q, KeyEventType.KeyDown, isShiftPressed = true)
        )

        subject.onSingleClick(subject.items().first())
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun onSingleClickWithUnSelection() {
        every { anyConstructed<FileListItemFactory>().invoke(any(), any()) } answers {
            FileListItem(mockk())
        }
        subject.start(
            listOf(
                mockk<Path>(),
                mockk<Path>(),
                mockk<Path>(),
                mockk<Path>()
            )
        )
        subject.onSingleClick(subject.items().last())

        subject.onSingleClick(subject.items().first())
        assertEquals(1, subject.items().filter { it.selected }.size)
    }

    @Test
    fun onLongClick() {
        val item = FileListItem(mockk(), editable = true)

        subject.onLongClick(item)

        verify(inverse = true) { mainViewModel.openFile(any()) }
        verify { mainViewModel.edit(any(), any()) }
    }

    @Test
    fun onLongClickUnEditableItem() {
        subject.start(listOf(mockk<Path>(relaxed = true)))

        subject.onLongClick(subject.items().first())

        verify { mainViewModel.openFile(any()) }
        verify(inverse = true) { mainViewModel.edit(any(), any()) }
    }

    @Test
    fun onDoubleClick() {
        val item = FileListItem(mockk(), selected = false, editable = true)

        subject.onDoubleClick(item)

        verify(inverse = true) { mainViewModel.openFile(any()) }
        verify { mainViewModel.hideArticleList() }
        verify { mainViewModel.edit(any(), any()) }
    }

    @Test
    fun onDoubleClickUnEditableItem() {
        subject.start(listOf(mockk<Path>(relaxed = true)))

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

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun onPointerEvent() {
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        every { pointerInputChange.changedToDownIgnoreConsumed() } returns true
        val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
        every { pointerEvent.button } returns PointerButton.Secondary
        subject.start(
            listOf(
                mockk<Path>(),
                mockk<Path>()
            )
        )
        val fileListItem = subject.items()[1]

        subject.onPointerEvent(pointerEvent, 1)

        assertTrue(subject.openingDropdown(fileListItem))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun noopOnPointerEvent() {
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        every { pointerInputChange.changedToDownIgnoreConsumed() } returns false
        val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
        every { pointerEvent.button } returns PointerButton.Secondary
        subject.start(
            listOf(
                mockk<Path>(),
                mockk<Path>()
            )
        )
        val fileListItem = subject.items()[1]

        subject.onPointerEvent(pointerEvent, 0)

        assertFalse(subject.openingDropdown(fileListItem))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun noopOnPointerEventWithOutOfBoundsIndex() {
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        every { pointerInputChange.changedToDownIgnoreConsumed() } returns false
        val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
        every { pointerEvent.button } returns PointerButton.Secondary

        subject.onPointerEvent(pointerEvent, 1)

        verify { pointerEvent wasNot called }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun noopOnPointerEventOnOpeningDropdown() {
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        every { pointerInputChange.changedToDownIgnoreConsumed() } returns true
        val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
        every { pointerEvent.button } returns PointerButton.Secondary
        subject.start(
            listOf(
                mockk<Path>(relaxed = true),
                mockk<Path>(relaxed = true)
            )
        )
        val fileListItem = subject.items()[1]
        subject.openDropdown(fileListItem)

        subject.onPointerEvent(pointerEvent, 1)

        assertTrue(subject.openingDropdown(fileListItem))
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun noopOnPointerEventWithOtherButton() {
        val pointerInputChange = mockk<PointerInputChange>()
        every { pointerInputChange.previousPressed } returns false
        every { pointerInputChange.pressed } returns true
        every { pointerInputChange.changedToDownIgnoreConsumed() } returns true
        val pointerEvent = spyk(PointerEvent(listOf(pointerInputChange)))
        every { pointerEvent.button } returns PointerButton.Primary
        subject.start(
            listOf(
                mockk<Path>(),
                mockk<Path>()
            )
        )
        val fileListItem = subject.items()[1]

        subject.onPointerEvent(pointerEvent, 1)

        assertFalse(subject.openingDropdown(fileListItem))
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

        verify { anyConstructed<ClipboardPutterService>().invoke("test") }
    }

    @OptIn(InternalComposeUiApi::class)
    @Test
    fun selectedFiles() {
        every { anyConstructed<FileListItemFactory>().invoke(any(), any()) } answers {
            FileListItem(mockk())
        }
        subject.start(
            listOf(
                mockk<Path>(),
                mockk<Path>(),
                mockk<Path>(),
                mockk<Path>()
            )
        )
        subject.onSingleClick(subject.items().last())

        assertEquals(1, subject.selectedFiles().size)
    }

}