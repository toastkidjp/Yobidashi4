package jp.toastkid.yobidashi4.presentation.number

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.number.NumberPlaceGame
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.repository.number.GameRepository
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class NumberPlaceViewKtTest {

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var gameRepository: GameRepository

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier = null) { viewModel } bind(MainViewModel::class)
                    single(qualifier = null) { gameRepository } bind(GameRepository::class)
                    single(qualifier = null) { setting } bind(Setting::class)
                }
            )
        }

        every { viewModel.showSnackbar(any(), any(), any()) }
        every { setting.getMaskingCount() } returns 10
        every { setting.setMaskingCount(any()) } just Runs
        every { gameRepository.load(any()) } returns NumberPlaceGame()
        every { gameRepository.save(any(), any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun numberPlaceView() {
        runDesktopComposeUiTest {
            setContent {
                NumberPlaceView()
            }
        }
    }

}