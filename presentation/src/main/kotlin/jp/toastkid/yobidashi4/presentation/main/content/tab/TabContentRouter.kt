/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.content.tab

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.toastkid.yobidashi4.domain.model.tab.BarcodeToolTab
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.model.tab.ClusteringToolTab
import jp.toastkid.yobidashi4.domain.model.tab.CompoundInterestCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.ConverterToolTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorSettingTab
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.FileRenameToolTab
import jp.toastkid.yobidashi4.domain.model.tab.FileTab
import jp.toastkid.yobidashi4.domain.model.tab.InputHistoryTab
import jp.toastkid.yobidashi4.domain.model.tab.LoanCalculatorTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.NotificationListTab
import jp.toastkid.yobidashi4.domain.model.tab.NumberPlaceGameTab
import jp.toastkid.yobidashi4.domain.model.tab.PhotoTab
import jp.toastkid.yobidashi4.domain.model.tab.RouletteToolTab
import jp.toastkid.yobidashi4.domain.model.tab.SettingEditorTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.model.tab.TextFileViewerTab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.tab.WebHistoryTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.barcode.BarcodeToolTabView
import jp.toastkid.yobidashi4.presentation.calendar.CalendarView
import jp.toastkid.yobidashi4.presentation.chat.ChatTabView
import jp.toastkid.yobidashi4.presentation.compound.CompoundInterestCalculatorView
import jp.toastkid.yobidashi4.presentation.converter.ConverterToolTabView
import jp.toastkid.yobidashi4.presentation.editor.EditorTabView
import jp.toastkid.yobidashi4.presentation.editor.setting.EditorSettingComponent
import jp.toastkid.yobidashi4.presentation.input.InputHistoryView
import jp.toastkid.yobidashi4.presentation.loan.LoanCalculatorView
import jp.toastkid.yobidashi4.presentation.log.viewer.TextFileViewerTabView
import jp.toastkid.yobidashi4.presentation.main.content.FileListView
import jp.toastkid.yobidashi4.presentation.main.content.TableView
import jp.toastkid.yobidashi4.presentation.markdown.MarkdownTabView
import jp.toastkid.yobidashi4.presentation.number.NumberPlaceView
import jp.toastkid.yobidashi4.presentation.photo.PhotoTabView
import jp.toastkid.yobidashi4.presentation.setting.SettingEditorView
import jp.toastkid.yobidashi4.presentation.tool.clustering.ClusteringToolTabView
import jp.toastkid.yobidashi4.presentation.tool.file.FileRenameToolView
import jp.toastkid.yobidashi4.presentation.tool.notification.NotificationListTabView
import jp.toastkid.yobidashi4.presentation.tool.roulette.RouletteToolTabView
import jp.toastkid.yobidashi4.presentation.web.WebTabView
import jp.toastkid.yobidashi4.presentation.web.bookmark.WebBookmarkTabView
import jp.toastkid.yobidashi4.presentation.web.history.WebHistoryView

private val DefaultTabContentRegistry = TabContentRegistry.Builder()
    .register(FileTab::class) { FileListView(it.items, Modifier) }
    .register(TableTab::class) { TableView(it) }
    .register(EditorTab::class) { EditorTabView(it) }
    .register(EditorSettingTab::class) { EditorSettingComponent(modifier = Modifier) }
    .register(MarkdownPreviewTab::class) { MarkdownTabView(it, Modifier) }
    .register(CalendarTab::class) { CalendarView(it) }
    .register(WebTab::class) { WebTabView(it) }
    .register(WebBookmarkTab::class) { WebBookmarkTabView(it) }
    .register(WebHistoryTab::class) { WebHistoryView(it) }
    .register(TextFileViewerTab::class) { TextFileViewerTabView(it) }
    .register(ChatTab::class) { ChatTabView(it) }
    .register(PhotoTab::class) { PhotoTabView(it) }
    .register(InputHistoryTab::class) { InputHistoryView(it) }
    .register(CompoundInterestCalculatorTab::class) { CompoundInterestCalculatorView() }
    .register(FileRenameToolTab::class) { FileRenameToolView() }
    .register(RouletteToolTab::class) { RouletteToolTabView() }
    .register(NumberPlaceGameTab::class) { NumberPlaceView() }
    .register(LoanCalculatorTab::class) { LoanCalculatorView() }
    .register(ConverterToolTab::class) { ConverterToolTabView() }
    .register(BarcodeToolTab::class) { BarcodeToolTabView() }
    .register(NotificationListTab::class) { NotificationListTabView() }
    .register(SettingEditorTab::class) { SettingEditorView() }
    .register(ClusteringToolTab::class) { ClusteringToolTabView() }
    .build()

@Composable
fun TabContentRouter(
    tab: Tab?,
    registry: TabContentRegistry = DefaultTabContentRegistry
) {
    registry.Render(tab)
}