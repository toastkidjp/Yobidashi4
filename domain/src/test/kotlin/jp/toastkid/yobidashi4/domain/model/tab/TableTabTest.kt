package jp.toastkid.yobidashi4.domain.model.tab

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.aggregation.FindResult
import jp.toastkid.yobidashi4.domain.model.aggregation.MovieMemoExtractorResult
import jp.toastkid.yobidashi4.domain.model.aggregation.OutgoAggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.StocksAggregationResult
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TableTabTest {

    @Test
    fun test() {
        val tab = TableTab("test", mockk())

        assertNotNull(tab.title())
        assertTrue(tab.closeable())
    }

    @Test
    fun iconPath() {
        assertTrue(TableTab("test", MovieMemoExtractorResult()).iconPath()!!.startsWith("images/icon/"))
        assertTrue(TableTab("test", OutgoAggregationResult("test")).iconPath()!!.startsWith("images/icon/"))
        assertTrue(TableTab("test", FindResult("test")).iconPath()!!.startsWith("images/icon/"))
        assertTrue(TableTab("test", StocksAggregationResult()).iconPath()!!.startsWith("images/icon/"))
        assertNull(TableTab("test", mockk()).iconPath())
    }

    @Test
    fun reload() {
        val action = mockk<() -> Unit>()
        every { action.invoke() } just Runs
        val tab = TableTab("test", mockk(), reloadAction = action)

        tab.reload()

        verify(exactly = 1) { action.invoke() }
    }

    @Test
    fun items() {
        val items = MovieMemoExtractorResult()
        val tableTab = TableTab("test", items, true, mockk())

        assertSame(items, tableTab.items())
    }

}