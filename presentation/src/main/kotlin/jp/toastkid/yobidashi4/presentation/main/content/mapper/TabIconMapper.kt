package jp.toastkid.yobidashi4.presentation.main.content.mapper

import java.nio.file.Files
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
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.tab.WebHistoryTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_asset_management
import jp.toastkid.yobidashi4.library.resources.ic_barcode
import jp.toastkid.yobidashi4.library.resources.ic_bookmark
import jp.toastkid.yobidashi4.library.resources.ic_calendar
import jp.toastkid.yobidashi4.library.resources.ic_chat
import jp.toastkid.yobidashi4.library.resources.ic_converter
import jp.toastkid.yobidashi4.library.resources.ic_edit
import jp.toastkid.yobidashi4.library.resources.ic_elevation
import jp.toastkid.yobidashi4.library.resources.ic_find_in_page
import jp.toastkid.yobidashi4.library.resources.ic_history
import jp.toastkid.yobidashi4.library.resources.ic_home
import jp.toastkid.yobidashi4.library.resources.ic_log
import jp.toastkid.yobidashi4.library.resources.ic_markdown
import jp.toastkid.yobidashi4.library.resources.ic_movie
import jp.toastkid.yobidashi4.library.resources.ic_music
import jp.toastkid.yobidashi4.library.resources.ic_notification
import jp.toastkid.yobidashi4.library.resources.ic_number_place
import jp.toastkid.yobidashi4.library.resources.ic_payments
import jp.toastkid.yobidashi4.library.resources.ic_rename
import jp.toastkid.yobidashi4.library.resources.ic_search
import jp.toastkid.yobidashi4.library.resources.ic_shuffle
import jp.toastkid.yobidashi4.library.resources.ic_text
import jp.toastkid.yobidashi4.library.resources.ic_web
import jp.toastkid.yobidashi4.library.resources.icon
import org.jetbrains.compose.resources.DrawableResource

class TabIconMapper {

    operator fun invoke(tab: Tab): DrawableResource? = when(tab) {
        is BarcodeToolTab -> Res.drawable.ic_barcode
        is CalendarTab -> Res.drawable.ic_calendar
        is ChatTab -> Res.drawable.ic_chat
        is CompoundInterestCalculatorTab -> Res.drawable.ic_elevation
        is ConverterToolTab -> Res.drawable.ic_converter
        is EditorTab -> Res.drawable.ic_edit
        is EditorSettingTab -> Res.drawable.ic_edit
        is FileTab -> if (tab.type == FileTab.Type.MUSIC) Res.drawable.ic_music else Res.drawable.ic_search
        is FileRenameToolTab -> Res.drawable.ic_rename
        is LoanCalculatorTab -> Res.drawable.ic_home
        is MarkdownPreviewTab -> Res.drawable.ic_markdown
        is PhotoTab -> null
        is RouletteToolTab -> Res.drawable.ic_shuffle
        is WebBookmarkTab -> Res.drawable.ic_bookmark
        is WebHistoryTab -> Res.drawable.ic_history
        is NotificationListTab -> Res.drawable.ic_notification
        is NumberPlaceGameTab -> Res.drawable.ic_number_place
        is TableTab -> when (tab.items()) {
            is MovieMemoExtractorResult -> Res.drawable.ic_movie
            is OutgoAggregationResult -> Res.drawable.ic_payments
            is FindResult -> Res.drawable.ic_find_in_page
            is StocksAggregationResult -> Res.drawable.ic_asset_management
            else -> Res.drawable.icon
        }
        is TextFileViewerTab -> if (tab.path().startsWith("temporary/logs/")) Res.drawable.ic_log else Res.drawable.ic_text
        is WebTab -> mapWebTab(tab)
        else -> Res.drawable.icon
    }

    private fun mapWebTab(tab: WebTab): DrawableResource? {
        val url = tab.url()
        if (url.isEmpty()) {
            return Res.drawable.ic_web
        }

        val faviconFolder = WebIcon()
        faviconFolder.makeFolderIfNeed()
        val iconPath = faviconFolder.find(url) ?: return Res.drawable.ic_web
        if (Files.exists(iconPath)) {
            return null
        }
        return Res.drawable.ic_web
    }

}