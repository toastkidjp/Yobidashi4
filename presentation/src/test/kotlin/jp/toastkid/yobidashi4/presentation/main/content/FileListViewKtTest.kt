package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
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
}