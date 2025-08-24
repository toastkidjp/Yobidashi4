package jp.toastkid.yobidashi4.presentation.time

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class WorldTimeViewKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { mainViewModel.showSnackbar(any(), any(), any()) } just Runs

        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun worldTimeView() {
        runDesktopComposeUiTest {
            setContent {
                WorldTimeView(Modifier)
            }

            onNodeWithContentDescription("Asia/Tokyo", useUnmergedTree = true)
                .performClick()

            verify { mainViewModel.showSnackbar(any(), any(), any()) }

            onNodeWithContentDescription("Timezone chooser", useUnmergedTree = true)
                .performClick()

            onNodeWithContentDescription("Timezone chooser's item UTC", useUnmergedTree = true)
                .performMouseInput {
                    click()
                }

            onNodeWithContentDescription("Hour chooser", useUnmergedTree = true)
                .performClick()

            onNodeWithContentDescription("Hour chooser's item 1", useUnmergedTree = true)
                .performMouseInput {
                    click()
                }

            onNodeWithContentDescription("Minute chooser", useUnmergedTree = true)
                .performClick()

            onNodeWithContentDescription("Minute chooser's item 1", useUnmergedTree = true)
                .performMouseInput {
                    click()
                }
        }
    }
}