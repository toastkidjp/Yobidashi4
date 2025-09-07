package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItem
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

class FileListViewKtTest {

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
        every { mainViewModel.hideArticleList() } just Runs

        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.size(any()) } returns 19223

        mockkConstructor(FileListViewModel::class)
        every { anyConstructed<FileListViewModel>().openingDropdown(any()) } returns false
        every { anyConstructed<FileListViewModel>().items() } returns listOf(makeMockElement("test-list-item"))
        every { anyConstructed<FileListViewModel>().onSingleClick(any()) } just Runs
        every { anyConstructed<FileListViewModel>().onLongClick(any()) } just Runs
        every { anyConstructed<FileListViewModel>().onDoubleClick(any()) } just Runs
        every { anyConstructed<FileListViewModel>().openFile(any()) } just Runs
        every { anyConstructed<FileListViewModel>().edit(any()) } just Runs
        every { anyConstructed<FileListViewModel>().preview(any()) } just Runs
        every { anyConstructed<FileListViewModel>().slideshow(any()) } just Runs
        every { anyConstructed<FileListViewModel>().clipText(any()) } just Runs
        every { anyConstructed<FileListViewModel>().onPointerEvent(any(), any()) } just Runs
        every { anyConstructed<FileListViewModel>().start(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun fileListView() {
        val path = mockk<Path>()
        every { path.nameWithoutExtension } returns "none-sub-text.txt"
        val element = mockk<FileListItem>()
        every { element.path } returns path
        every { element.editable } returns true
        every { element.selected } returns false
        every { element.subText() } returns null

        every { anyConstructed<FileListViewModel>().items() } returns listOf(
            makeMockElement("test-list-item1", true),
            makeMockElement("test-list-item2"),
            makeMockElement("test-list-item3"),
            makeMockElement("test-list-item4"),
            element
        )

        runDesktopComposeUiTest {
            setContent {
                FileListView(emptyList())
            }

            onNode(hasText("test-list-item1"), useUnmergedTree = true)
                .assertExists("Not exists!")
                .performClick()
                .performKeyInput {
                    pressKey(Key.DirectionUp)
                    pressKey(Key.DirectionDown)
                    pressKey(Key.Enter)
                }
                .performMouseInput {
                    enter()
                    exit()
                }
            verify { anyConstructed<FileListViewModel>().edit(any()) }

            onNodeWithContentDescription("Reload file list", useUnmergedTree = true)
                .performClick()
            verify { anyConstructed<FileListViewModel>().start(any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun mouseEvent() {
        runDesktopComposeUiTest {
            setContent {
                FileListView(emptyList())
            }

            val node = onNodeWithContentDescription("test-list-item", useUnmergedTree = true)

            node.performMouseInput {
                longClick()
                doubleClick()
            }
            verify { anyConstructed<FileListViewModel>().onLongClick(any()) }
            verify { anyConstructed<FileListViewModel>().onDoubleClick(any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun withDropdown() {
        every { anyConstructed<FileListViewModel>().openingDropdown(any()) } returns true

        runDesktopComposeUiTest {
            setContent {
                FileListView(emptyList())
            }

            onNode(hasText("Open"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<FileListViewModel>().openFile(any()) }

            onNode(hasText("Edit"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<FileListViewModel>().edit(any()) }

            onNode(hasText("Preview"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<FileListViewModel>().preview(any()) }

            onNode(hasText("Open background"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<FileListViewModel>().openFile(any()) }

            onNode(hasText("Slideshow"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<FileListViewModel>().slideshow(any()) }

            onNode(hasText("Copy title"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<FileListViewModel>().clipText(any()) }

            onNode(hasText("Clip internal link"), useUnmergedTree = true).onParent().performClick()
            verify { anyConstructed<FileListViewModel>().clipText(any()) }
        }
    }

    private fun makeMockElement(fileName: String, selected: Boolean = false): FileListItem {
        val path = mockk<Path>()
        every { path.nameWithoutExtension } returns fileName
        val element = mockk<FileListItem>()
        every { element.path } returns path
        every { element.editable } returns true
        every { element.selected } returns selected
        every { element.subText() } returns "2024-01-22"
        return element
    }

}