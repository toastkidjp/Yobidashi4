package jp.toastkid.yobidashi4.presentation.main.content.mapper

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.aggregation.FindResult
import jp.toastkid.yobidashi4.domain.model.aggregation.MovieMemoExtractorResult
import jp.toastkid.yobidashi4.domain.model.aggregation.OutgoAggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.StocksAggregationResult
import jp.toastkid.yobidashi4.domain.model.tab.BarcodeToolTab
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.model.tab.CompoundInterestCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.ConverterToolTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorSettingTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.FileRenameToolTab
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.NotificationListTab
import jp.toastkid.yobidashi4.domain.model.tab.NumberPlaceGameTab
import jp.toastkid.yobidashi4.domain.model.tab.PhotoTab
import jp.toastkid.yobidashi4.domain.model.tab.RouletteToolTab
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.tab.WebHistoryTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TabIconMapperTest {

    private lateinit var subject: TabIconMapper

    @BeforeEach
    fun setUp() {
        subject = TabIconMapper()

        mockkConstructor(WebIcon::class)
        every { anyConstructed<WebIcon>().makeFolderIfNeed() } just Runs
        every { anyConstructed<WebIcon>().find(any()) } returns mockk()
        every { anyConstructed<WebIcon>().faviconFolder() } returns mockk()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val musicFileTab = mockk<FileTab>()
        every { musicFileTab.type } returns FileTab.Type.MUSIC
        val findFileTab = mockk<FileTab>()
        every { findFileTab.type } returns FileTab.Type.FIND
        val tableTab = mockk<TableTab>()
        every { tableTab.items() } returns mockk()
        val movieTableTab = mockk<TableTab>()
        every { movieTableTab.items() } returns mockk<MovieMemoExtractorResult>()
        val outgoTableTab = mockk<TableTab>()
        every { outgoTableTab.items() } returns mockk<OutgoAggregationResult>()
        val findTableTab = mockk<TableTab>()
        every { findTableTab.items() } returns mockk<FindResult>()
        val stockTableTab = mockk<TableTab>()
        every { stockTableTab.items() } returns mockk<StocksAggregationResult>()
        val logTextFileViewerTab = mockk<TextFileViewerTab>()
        every { logTextFileViewerTab.path() } returns Path.of("temporary/logs/")
        val textFileViewerTab = mockk<TextFileViewerTab>()
        every { textFileViewerTab.path() } returns Path.of("temporary/text/")
        val emptyWebTab = mockk<WebTab>()
        every { emptyWebTab.url() } returns ""
        val webTab = mockk<WebTab>()
        every { webTab.url() } returns "test"
        val tabs = setOf(
            mockk<BarcodeToolTab>(),
            mockk<CalendarTab>(),
            mockk<ChatTab>(),
            mockk<CompoundInterestCalculatorTab>(),
            mockk<ConverterToolTab>(),
            mockk<EditorSettingTab>(),
            mockk<EditorTab>(),
            musicFileTab,
            findFileTab,
            mockk<FileRenameToolTab>(),
            mockk<LoanCalculatorTab>(),
            mockk<MarkdownPreviewTab>(),
            mockk<NotificationListTab>(),
            mockk<NumberPlaceGameTab>(),
            mockk<PhotoTab>(),
            mockk<RouletteToolTab>(),
            tableTab,
            movieTableTab,
            outgoTableTab,
            findTableTab,
            stockTableTab,
            logTextFileViewerTab,
            textFileViewerTab,
            mockk<WebBookmarkTab>(),
            mockk<WebHistoryTab>(),
            emptyWebTab,
            webTab
        )
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns false

        val drawables = tabs.map(subject::invoke)

        assertEquals(tabs.size, drawables.size)
    }

    @Test
    fun otherCase() {
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        val webTab = mockk<WebTab>()
        every { webTab.url() } returns "test"

        assertNull(subject(webTab))
    }

    @Test
    fun otherCase2() {
        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        val webTab = mockk<WebTab>()
        every { webTab.url() } returns "test"
        every { anyConstructed<WebIcon>().find(any()) } returns mockk()

        assertNull(subject(webTab))
    }

}
