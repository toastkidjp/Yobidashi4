package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import java.time.DayOfWeek
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DayLabelViewKtTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun dayLabelView() {
        runDesktopComposeUiTest {
            setContent {
                DayLabelView(
                    1,
                    DayOfWeek.MONDAY,
                    "test",
                    true,
                    false,
                    Modifier
                )
                DayLabelView(
                    1,
                    DayOfWeek.MONDAY,
                    "test",
                    false,
                    true,
                    Modifier
                )
                DayLabelView(
                    1,
                    DayOfWeek.TUESDAY,
                    "test",
                    false,
                    false,
                    Modifier
                )
                DayLabelView(
                    1,
                    DayOfWeek.SATURDAY,
                    "test",
                    false,
                    false,
                    Modifier
                )
                DayLabelView(
                    1,
                    DayOfWeek.SUNDAY,
                    "test",
                    false,
                    false,
                    Modifier
                )
            }
        }
    }
}