package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import jp.toastkid.yobidashi4.domain.model.aggregation.StepsAggregationResult
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TableViewKtTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tableView() {
        runDesktopComposeUiTest {
            setContent {
                val result = StepsAggregationResult()
                result.put("2022-12-22", 1200, 12)
                result.put("2022-12-23", 1240, 12)
                result.put("2022-12-24", 1230, 12)

                TableView(result)
            }
        }
    }
}