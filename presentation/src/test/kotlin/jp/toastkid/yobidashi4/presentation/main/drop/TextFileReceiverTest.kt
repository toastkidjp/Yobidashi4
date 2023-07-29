package jp.toastkid.yobidashi4.presentation.main.drop

import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import java.nio.file.Path
import jp.toastkid.yobidashi4.presentation.composable.callComposable
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
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
        every { mainViewModel.droppedPathFlow() } returns flowOf(path)
        every { path.fileName } returns path
        every { path.toString() } returns "test.txt"

        textFileReceiver = TextFileReceiver(Dispatchers.Unconfined)
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        clearAllMocks()
    }

    @Test
    fun launch() {
        callComposable {
            textFileReceiver.launch()

            verify { mainViewModel.droppedPathFlow() }
        }
    }

}