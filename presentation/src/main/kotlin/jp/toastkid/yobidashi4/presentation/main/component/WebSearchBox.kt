/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightDropdownMenuItem
import jp.toastkid.yobidashi4.presentation.component.InputTextField
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun WebSearchBox() {
    val viewModel = remember { WebSearchBoxViewModel() }

    Surface(
        modifier = Modifier.wrapContentHeight().fillMaxWidth(),
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.wrapContentSize()
        ) {
            Text("x", modifier = Modifier
                .padding(start = 4.dp)
                .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                .clickable { viewModel.setShowWebSearch(false) }
                .padding(8.dp)
                .semantics { contentDescription = "Close web search box." }
            )

            Box(
                modifier = Modifier
                    .clickable(onClick = viewModel::setOpenDropdown)
                    .semantics { contentDescription = "Switch dropdown menu." }
            ) {
                Surface(elevation = 4.dp) {
                    Image(
                        painterResource(viewModel.currentIconPath()),
                        contentDescription = viewModel.currentSiteName(),
                        colorFilter = if (viewModel.currentTint()) ColorFilter.tint(MaterialTheme.colors.secondary) else null,
                        modifier = Modifier.size(64.dp).padding(8.dp)
                    )
                }

                DropdownMenu(
                    expanded = viewModel.openingDropdown(),
                    offset = DpOffset(0.dp, viewModel.makeVerticalOffset()),
                    onDismissRequest = viewModel::closeDropdown
                ) {
                    if (viewModel.containsSwingContent()) {
                        LazyRow(modifier = Modifier.width(600.dp).height(60.dp)) {
                            items(viewModel.items()) {
                                Image(
                                    painterResource(it.icon),
                                    contentDescription = it.label,
                                    colorFilter = if (it.useTint) ColorFilter.tint(MaterialTheme.colors.secondary) else null,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(horizontal = 8.dp)
                                        .clickable {
                                            viewModel.choose(it)
                                        }
                                )
                            }
                        }
                        return@DropdownMenu
                    }
                    viewModel.items().forEach {
                        HoverHighlightDropdownMenuItem(
                            it.label,
                            drawableResource = it.icon,
                            useTint = it.useTint,
                            iconSize = 48.dp
                        ) {
                            viewModel.choose(it)
                        }
                    }
                }
            }

            InputTextField(
                viewModel.query(),
                "Please would you input web search keyword?",
                onValueChange = viewModel::onValueChange,
                onSearch = {
                    viewModel.invokeSearch()
                },
                clearButton = viewModel::clearInput,
                viewModel.shouldShowInputHistory(),
                suggestions = viewModel.inputHistories(),
                suggestionConsumer = viewModel::putText,
                viewModel::deleteInputHistoryItem,
                viewModel::clearInputHistory,
                viewModel::onFocusChanged,
                modifier = Modifier
                    .focusRequester(viewModel.focusRequester())
                    .onKeyEvent(viewModel::onKeyEvent)
            )

            if (viewModel.existsResult()) {
                Text(
                    " = ",
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                SelectionContainer {
                    Text(
                        viewModel.result(),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            Button(
                onClick = viewModel::invokeSearch
            ) {
                Text("Search")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = viewModel::switchSaveSearchHistory)
                    .semantics { contentDescription = "Switch search history" }
            ) {
                Checkbox(
                    viewModel.saveSearchHistory(),
                    viewModel::setSaveSearchHistory,
                    modifier = Modifier.semantics { contentDescription = "save search history" }
                )

                Text("Save search history")
            }

            LaunchedEffect(viewModel.showWebSearch()) {
                viewModel.start()
            }
        }
    }
}
