/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.model.tab.Reloadable
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.model.tab.WebBookmarkTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.tab.WithFilePath
import jp.toastkid.yobidashi4.domain.model.web.bookmark.WebBookmarkPath
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_down
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightColumn
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightDropdownMenuItem
import jp.toastkid.yobidashi4.presentation.component.ReorderableTabRow
import jp.toastkid.yobidashi4.presentation.component.TabIcon
import jp.toastkid.yobidashi4.presentation.lib.annotation.ExcludeCoverageCalculation
import jp.toastkid.yobidashi4.presentation.main.content.tab.DefaultTabContentRegistry
import jp.toastkid.yobidashi4.presentation.main.content.tab.TabContentRegistry
import jp.toastkid.yobidashi4.presentation.main.content.tab.TabContentRouter
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

@ExcludeCoverageCalculation
@Composable
internal fun TabsView(
    modifier: Modifier = Modifier
) {
    TabsView(modifier, koinInject(), DefaultTabContentRegistry)
}

@Composable
internal fun TabsView(
    modifier: Modifier = Modifier,
    viewModel: TabsViewModel,
    registry: TabContentRegistry
) {
    if (viewModel.tabs().isEmpty()) {
        return
    }

    val primaryColor = MaterialTheme.colors.onPrimary

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.75f).copy(alpha = 0.75f)

            val rowHeight = remember { mutableStateOf(0) }
            val switchHeight = with(LocalDensity.current) {
                rowHeight.value.toDp()
            }

            Box(modifier = Modifier
                .drawBehind {
                    drawRect(backgroundColor)
                }
                .height(switchHeight)
            ) {
                Icon(
                    painterResource(Res.drawable.ic_down),
                    contentDescription = "Tabs menu",
                    modifier = Modifier.clickable(onClick = viewModel::openTabsOptionMenu)
                        .align(Alignment.Center)
                )

                DropdownMenu(
                    viewModel.isOpenTabsOptionMenu().collectAsState().value,
                    viewModel::closeTabsOptionMenu
                ) {
                    HoverHighlightColumn(
                        modifier = Modifier.clickable(onClick = viewModel::closeAllTabs)
                            .semantics {
                                contentDescription = "tab-option-root-closeAllTabs"
                            }
                    ) {
                        Text("Close all", modifier = Modifier.padding(8.dp))
                    }

                    viewModel.tabs().forEachIndexed { index, tab ->
                        HoverHighlightColumn(
                            modifier = Modifier.clickable {
                                viewModel.setSelectedIndex(index)
                            }
                                .semantics {
                                    contentDescription = "tab-option-${tab.title()}"
                                }
                        ) {
                            TabContent(
                                tab,
                                tab::title,
                                { viewModel.openingDropdown(tab) },
                                viewModel::closeOtherTabs,
                                viewModel::openFile,
                                viewModel::slideshow,
                                viewModel::clipText,
                                viewModel::edit,
                                viewModel::exportTable,
                                viewModel::exportChat,
                                viewModel::closeDropdown,
                                viewModel::calculateTabWidth,
                                "Close button $index",
                            ) { viewModel.removeTabAt(index) }
                        }
                    }
                }
            }

            ReorderableTabRow(
                viewModel.tabs(),
                selectedTabIndex = viewModel.selectedTabIndex(),
                backgroundColor = backgroundColor,
                indicator = { tabPositions ->
                    val currentTabIndex = viewModel.currentTabIndex(tabPositions.size)

                    val currentTabPosition = tabPositions.getOrNull(currentTabIndex) ?: return@ReorderableTabRow

                    Divider(modifier = Modifier
                        .tabIndicatorOffset(currentTabPosition)
                        .height(2.dp)
                        .clip(RoundedCornerShape(8.dp)) // clip modifier not working
                        .padding(horizontal = 4.dp)
                        .drawBehind {
                            drawRect(primaryColor)
                        }
                    )
                },
                onTabsReordered = { a, b -> viewModel.swapTab(a, b) },
                modifier = Modifier
                    .onGloballyPositioned {
                        rowHeight.value = it.size.height
                    }
            ) { index, tab ->
                val titleState = remember { mutableStateOf(tab.title()) }
                LaunchedEffect("${index}_${tab.hashCode()}") {
                    titleState.value = tab.title()

                    tab.update().collect {
                        titleState.value = tab.title()
                    }
                }

                Tab(
                    selected = viewModel.isSelectedIndex(index),
                    onClick = { viewModel.setSelectedIndex(index) },
                    modifier = Modifier
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                viewModel.onPointerEvent(awaitPointerEvent(), tab)
                            }
                        }
                        .semantics { contentDescription = "tab_${index}" }
                        .padding(4.dp)
                ) {
                    TabContent(
                        tab,
                        { titleState.value },
                        { viewModel.openingDropdown(tab) },
                        viewModel::closeOtherTabs,
                        viewModel::openFile,
                        viewModel::slideshow,
                        viewModel::clipText,
                        viewModel::edit,
                        viewModel::exportTable,
                        viewModel::exportChat,
                        viewModel::closeDropdown,
                        viewModel::calculateTabWidth,
                        "Close button $index",
                    ) { viewModel.removeTabAt(index) }
                }
            }
        }

        TabContentRouter(
            viewModel.currentTab(),
            registry
        )
    }
}

@Composable
private fun TabContent(
    tab: Tab,
    title: () -> String,
    openingDropdownMenu: (Tab) -> Boolean,
    closeOtherTabs: () -> Unit,
    openFile: (Path) -> Unit,
    slideshow: (Path) -> Unit,
    clipText: (String) -> Unit,
    edit: (Path) -> Unit,
    exportTable: (AggregationResult) -> Unit,
    exportChat: (ChatTab) -> Unit,
    closeDropdown: () -> Unit,
    calculateTabWidth: (Tab) -> Dp,
    closeButtonContentDescription: String,
    removeTabAt: () -> Unit
) {
    Box {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TabIcon(tab, Modifier.size(24.dp).padding(start = 4.dp))

            Text(
                title(),
                color = MaterialTheme.colors.onPrimary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .widthIn(max = calculateTabWidth(tab))
                    .padding(vertical = 8.dp)
                    .padding(start = 8.dp)
            )
            if (tab.closeable()) {
                Text(
                    "x",
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                        .clickable(onClick = removeTabAt)
                        .padding(8.dp)
                        .semantics { contentDescription = closeButtonContentDescription }
                )
            }
        }

        TabOptionMenu(
            { openingDropdownMenu(tab) },
            { tab },
            closeOtherTabs,
            openFile,
            slideshow,
            clipText,
            {
                edit(it.slideshowSourcePath())
            },
            {
                exportTable(it.items())
            },
            exportChat,
            closeDropdown
        )
    }
}

@Composable
private fun TabOptionMenu(
    openingDropdownMenu: () -> Boolean,
    tab: () -> Tab,
    closeOtherTabs: () -> Unit,
    openFile: (Path) -> Unit,
    slideshow: (Path) -> Unit,
    clipText: (String) -> Unit,
    edit: (MarkdownPreviewTab) -> Unit,
    exportTable: (TableTab) -> Unit,
    exportChat: (ChatTab) -> Unit,
    close: () -> Unit
) {
    val tab = tab()
    DropdownMenu(
        expanded = openingDropdownMenu(),
        onDismissRequest = close
    ) {
        HoverHighlightDropdownMenuItem("Copy title") {
            clipText(tab.title())
            close()
        }

        HoverHighlightDropdownMenuItem("Close other tabs") {
            closeOtherTabs()
            close()
        }

        if (tab is WebTab) {
            HoverHighlightDropdownMenuItem("Copy URL") {
                clipText(tab.url())
                close()
            }
        }

        if (tab is WebBookmarkTab) {
            HoverHighlightDropdownMenuItem("Modify") {
                openFile(WebBookmarkPath().getPath())
                close()
            }
        }

        if (tab is Reloadable) {
            HoverHighlightDropdownMenuItem("Reload") {
                tab.reload()
                close()
            }
        }

        if (tab is MarkdownPreviewTab) {
            HoverHighlightDropdownMenuItem("Edit") {
                edit(tab)
                close()
            }
        }

        if (tab is TableTab) {
            HoverHighlightDropdownMenuItem("Export table") {
                exportTable(tab)
                close()
            }
        }

        if (tab is WithFilePath) {
            HoverHighlightDropdownMenuItem("Open with editor") {
                openFile(tab.filePath())
                close()
            }

            HoverHighlightDropdownMenuItem("Clip internal link") {
                clipText("[[${tab.filePath().nameWithoutExtension}]]")
                close()
            }

            HoverHighlightDropdownMenuItem("Slideshow") {
                slideshow(tab.filePath())
                close()
            }
        }

        if (tab is ChatTab) {
            HoverHighlightDropdownMenuItem("Export chat") {
                exportChat(tab)
            }
        }
    }
}
