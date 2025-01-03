package jp.toastkid.yobidashi4.presentation.time

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WorldTimeViewKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(WorldTimeViewModel::class)
        every { anyConstructed<WorldTimeViewModel>().onClickItem(any()) } just Runs
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

            verify { anyConstructed<WorldTimeViewModel>().onClickItem(any()) }
        }
    }
}