package jp.toastkid.yobidashi4.presentation.main.drop

import androidx.compose.ui.test.ExperimentalTestApi
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Path
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class TextFileReceiverTest {

    private lateinit var textFileReceiver: TextFileReceiver

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var path2: Path

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }

        MockKAnnotations.init(this)
        every { mainViewModel.droppedPathFlow() } returns flowOf(
            path,
            path2,
            mameMockPath("test.md"),
            mameMockPath("test.log"),
            mameMockPath("test.java"),
            mameMockPath("test.kt"),
            mameMockPath("test.py"),
            mameMockPath("test")
        )
        every { path.fileName } returns path
        every { path.toString() } returns "test.txt"
        every { path2.fileName } returns path2
        every { path2.toString() } returns "test.exe"

        textFileReceiver = TextFileReceiver()
    }

    private fun mameMockPath(fileName: String): Path {
        val path = mockk<Path>()
        every { path.fileName } returns path
        every { path.toString() } returns fileName
        return path
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun launch() {
        every { mainViewModel.edit(any(), any()) } just Runs

        runBlocking {
            textFileReceiver.launch()

            verify { mainViewModel.droppedPathFlow() }
            verify(exactly = 6) { mainViewModel.edit(any(), any()) }
        }
    }

}