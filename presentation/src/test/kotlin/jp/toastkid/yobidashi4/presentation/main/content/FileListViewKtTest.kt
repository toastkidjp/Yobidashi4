package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import jp.toastkid.yobidashi4.presentation.main.content.data.FileListItem
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.nameWithoutExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class FileListViewKtTest {

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier = null) { mockk<MainViewModel>() } bind (MainViewModel::class)
                }
            )
        }

        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.size(any()) } returns 19223

        mockkConstructor(FileListViewModel::class)
        every { anyConstructed<FileListViewModel>().openingDropdown(any()) } returns false
        val element = mockk<FileListItem>()
        val path = mockk<Path>()
        every { path.fileName } returns path
        every { path.toString() } returns "test.md"
        every { element.path } returns path
        every { element.editable } returns true
        every { element.selected } returns true
        every { element.subText() } returns "2024-01-22"
        every { anyConstructed<FileListViewModel>().items() } returns listOf(element)
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
        every { path.nameWithoutExtension } returns "test"
        every { path.getLastModifiedTime() } returns FileTime.fromMillis(System.currentTimeMillis())

        runDesktopComposeUiTest {
            setContent {
                FileListView(listOf(path))
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun withDropdown() {
        val path = mockk<Path>()
        every { path.nameWithoutExtension } returns "test"
        every { path.getLastModifiedTime() } returns FileTime.fromMillis(System.currentTimeMillis())
        every { anyConstructed<FileListViewModel>().openingDropdown(any()) } returns true

        runDesktopComposeUiTest {
            setContent {
                FileListView(listOf(path))
            }
        }
    }

}