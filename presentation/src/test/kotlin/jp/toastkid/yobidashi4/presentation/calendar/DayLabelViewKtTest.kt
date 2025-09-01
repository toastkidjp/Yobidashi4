package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

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
                    listOf("test"),
                    true,
                    false,
                    Modifier
                )
                DayLabelView(
                    1,
                    DayOfWeek.MONDAY,
                    listOf("test"),
                    false,
                    true,
                    Modifier
                )
                DayLabelView(
                    1,
                    DayOfWeek.TUESDAY,
                    listOf("test"),
                    false,
                    false,
                    Modifier
                )
                DayLabelView(
                    1,
                    DayOfWeek.SATURDAY,
                    listOf("test"),
                    false,
                    false,
                    Modifier
                )
                DayLabelView(
                    1,
                    DayOfWeek.SUNDAY,
                    listOf("test"),
                    false,
                    false,
                    Modifier
                )
            }
        }
    }
}