package jp.toastkid.yobidashi4.presentation.main.tray

import androidx.compose.ui.window.TrayState
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Path
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MainTrayViewModelTest {

    private lateinit var subject: MainTrayViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var trayState: TrayState

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
        every { mainViewModel.trayState() } returns trayState
        every { mainViewModel.openFile(any()) } just Runs
        every { mainViewModel.openTab(any()) } just Runs

        mockkStatic(Path::class)
        every { Path.of(any<String>()) } returns mockk()

        subject = MainTrayViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun trayState() {
        assertSame(trayState, subject.trayState())
    }

    @Test
    fun openAppFolder() {
        subject.openAppFolder()

        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun openUserFolder() {
        subject.openUserFolder()

        verify { mainViewModel.openFile(any()) }
    }

    @Test
    fun openChat() {
        subject.openChat()

        verify { mainViewModel.openTab(any()) }
    }

}