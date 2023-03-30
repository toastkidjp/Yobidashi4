package jp.toastkid.yobidashi4.domain.model.tab

import io.mockk.mockk
import jp.toastkid.yobidashi4.domain.model.aggregation.MovieMemoExtractorResult
import jp.toastkid.yobidashi4.domain.model.aggregation.OutgoAggregationResult
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class TableTabTest {

    @Test
    fun iconPath() {
        assertTrue(TableTab("test", MovieMemoExtractorResult()).iconPath()!!.startsWith("images/icon/"))
        assertTrue(TableTab("test", OutgoAggregationResult("test")).iconPath()!!.startsWith("images/icon/"))
        assertNull(TableTab("test", mockk()).iconPath())
    }
}