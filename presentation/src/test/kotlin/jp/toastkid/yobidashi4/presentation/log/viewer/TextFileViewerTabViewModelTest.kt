package jp.toastkid.yobidashi4.presentation.log.viewer

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.nio.charset.MalformedInputException
import java.nio.file.Files
import java.nio.file.Path

class TextFileViewerTabViewModelTest {

    private lateinit var subject: TextFileViewerTabViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }
        every { mainViewModel.openFile(any()) } just Runs

        subject = TextFileViewerTabViewModel()

        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun keyboardScrollAction() {
        subject.keyboardScrollAction(CoroutineScope(Dispatchers.Unconfined), Key.DirectionUp, false)
    }

    @Test
    fun openWithKeyboardShortcut() {
        runBlocking {
            subject = spyk(subject)
            val focusRequester = mockk<FocusRequester>()
            every { focusRequester.requestFocus() } just Runs
            every { subject.focusRequester() } returns focusRequester
            every { Files.readAllLines(any()) } returns listOf("test")
            val path = mockk<Path>()
            subject.launch(path, Dispatchers.Unconfined)
            subject.keyboardScrollAction(CoroutineScope(Dispatchers.Unconfined), Key.O, true)

            verify { mainViewModel.openFile(path) }
        }
    }

    @Test
    fun focusRequester() {
        assertNotNull(subject.focusRequester())
    }

    @Test
    fun listState() {
        assertNotNull(subject.listState())
    }

    @Test
    fun lineNumber() {
        runBlocking {
            subject = spyk(subject)
            val focusRequester = mockk<FocusRequester>()
            every { focusRequester.requestFocus() } just Runs
            every { subject.focusRequester() } returns focusRequester
            every { Files.readAllLines(any()) } returns listOf(
                "test", "test", "test", "test", "test","test", "test", "test", "test", "test", "test"
            )
            subject.launch(mockk(), Dispatchers.Unconfined)

            println(subject.lineNumber(0))
            println(subject.lineNumber(10))
        }
    }

    @Test
    fun launch() {
        runBlocking {
            subject = spyk(subject)
            val focusRequester = mockk<FocusRequester>()
            every { focusRequester.requestFocus() } just Runs
            every { subject.focusRequester() } returns focusRequester
            every { Files.readAllLines(any()) } returns listOf("test")

            subject.launch(mockk(), Dispatchers.Unconfined)

            assertEquals(1, subject.textState().size)
            verify { focusRequester.requestFocus() }
        }
    }

    @Test
    fun launchWithException() {
        runBlocking {
            subject = spyk(subject)
            val focusRequester = mockk<FocusRequester>()
            every { focusRequester.requestFocus() } just Runs
            every { subject.focusRequester() } returns focusRequester
            every { Files.readAllLines(any()) } throws MalformedInputException(-1)

            subject.launch(mockk(), Dispatchers.Unconfined)

            assertTrue(subject.textState().isEmpty())
            verify { focusRequester.requestFocus() }
        }
    }

    @Test
    fun noopLaunch() {
        runBlocking {
            every { Files.exists(any()) } returns false

            subject.launch(mockk())
        }
    }
}