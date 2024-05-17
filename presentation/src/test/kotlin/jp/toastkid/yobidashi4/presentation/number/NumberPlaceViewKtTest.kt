package jp.toastkid.yobidashi4.presentation.number

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.MouseButton
import androidx.compose.ui.test.click
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
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
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicBoolean
import jp.toastkid.yobidashi4.domain.model.number.NumberPlaceGame
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.repository.number.GameRepository
import jp.toastkid.yobidashi4.domain.service.number.GameFileProvider
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
        val numberPlaceGame = NumberPlaceGame()
        numberPlaceGame.initialize(20)
        every { gameRepository.load(any()) } returns numberPlaceGame
        every { gameRepository.save(any(), any()) } just Runs

        mockkStatic(Files::class)
        every { Files.size(any()) } returns 1
        mockkConstructor(GameFileProvider::class, NumberPlaceViewModel::class)
        every { anyConstructed<GameFileProvider>().invoke() } returns mockk()

        every { anyConstructed<NumberPlaceViewModel>().openingMaskingCount() } returns false
        every { anyConstructed<NumberPlaceViewModel>().openingDropdown() } returns false
        every { anyConstructed<NumberPlaceViewModel>().openingCellOption(any(), any()) } returns false
        every { anyConstructed<NumberPlaceViewModel>().openCellOption(any(), any()) } just Runs
        every { anyConstructed<NumberPlaceViewModel>().place(any(), any(), any()) } just Runs
        every { anyConstructed<NumberPlaceViewModel>().onCellLongClick(any(), any()) } just Runs
        every { anyConstructed<NumberPlaceViewModel>().saveCurrentGame() } just Runs
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

            onAllNodesWithContentDescription("Masked cell").onFirst()
                .assertExists("Not found!")
                .performMouseInput {
                    click()
                    longClick()
                }

            verify { anyConstructed<NumberPlaceViewModel>().openCellOption(any(), any()) }
            verify { anyConstructed<NumberPlaceViewModel>().onCellLongClick(any(), any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun mainOption() {
        every { anyConstructed<NumberPlaceViewModel>().openingDropdown() } returns true

        runDesktopComposeUiTest {
            setContent {
                NumberPlaceView()
            }

            onNodeWithContentDescription("Surface", useUnmergedTree = true)
                .performMouseInput {
                    press(MouseButton.Secondary)
                    release(MouseButton.Secondary)

                    press(MouseButton.Primary)
                    release()
                }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun maskingCount() {
        every { anyConstructed<NumberPlaceViewModel>().openingMaskingCount() } returns true
        every { anyConstructed<NumberPlaceViewModel>().setMaskingCount(any()) } just Runs
        every { anyConstructed<NumberPlaceViewModel>().reloadGame() } just Runs
        every { anyConstructed<NumberPlaceViewModel>().closeMaskingCount() } just Runs

        runDesktopComposeUiTest {
            setContent {
                NumberPlaceView()
            }

            verify { anyConstructed<NumberPlaceViewModel>().openingMaskingCount() }

            onNodeWithContentDescription("masking_count_10", useUnmergedTree = true).onParent()
                .assertExists("Not found!")
                .performClick()

            verify { anyConstructed<NumberPlaceViewModel>().setMaskingCount(any()) }
            verify { anyConstructed<NumberPlaceViewModel>().reloadGame() }
            verify { anyConstructed<NumberPlaceViewModel>().closeMaskingCount() }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun withCellsDropdown() {
        val firstOnly = AtomicBoolean(true)
        every { anyConstructed<NumberPlaceViewModel>().openingCellOption(any(), any()) } answers {
            val get = firstOnly.getAndSet(false)
            get
        }

        runDesktopComposeUiTest {
            setContent {
                NumberPlaceView()
            }

            onAllNodesWithContentDescription("chooser_3", useUnmergedTree = true).onFirst()
                .assertExists("Not found!")
                .performClick()

            verify { anyConstructed<NumberPlaceViewModel>().place(any(), any(), any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun withLoading() {
        every { anyConstructed<NumberPlaceViewModel>().loading() } returns true

        runDesktopComposeUiTest {
            setContent {
                NumberPlaceView()
            }

            verify { anyConstructed<NumberPlaceViewModel>().loading() }
        }
    }

}