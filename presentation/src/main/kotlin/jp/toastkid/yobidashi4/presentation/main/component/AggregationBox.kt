package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.component.InputTextField

@Composable
internal fun AggregationBox() {
    val viewModel = remember { AggregationBoxViewModel() }

    Surface(
        modifier = Modifier.wrapContentHeight().fillMaxWidth().onKeyEvent(viewModel::onKeyEvent),
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
                .clickable { viewModel.switchAggregationBox(false) }
                .padding(8.dp)
            )

            Box(
                modifier = Modifier.clickable { viewModel.openChooser() }
            ) {
                Surface(elevation = 4.dp) {
                    /*Image(
                        painterResource(selectedSite.value.iconPath()),
                        contentDescription = selectedSite.value.siteName,
                        modifier = Modifier.size(64.dp).padding(8.dp)
                    )*/
                    Text(viewModel.selectedCategoryName(), modifier = Modifier.padding(start = 8.dp))
                }

                val swingContent = viewModel.isCurrentSwingContent()
                DropdownMenu(
                    expanded = viewModel.isOpeningChooser(),
                    offset = DpOffset(0.dp, if (swingContent) (-80).dp else 0.dp),
                    onDismissRequest = { viewModel.closeChooser() }
                ) {
                    if (swingContent) {
                        LazyRow(modifier = Modifier.width(300.dp).height(60.dp)) {
                            items(viewModel.items()) {
                               /* Image(
                                    painterResource(it.iconPath()),
                                    contentDescription = it.siteName,
                                    modifier = Modifier.size(48.dp).padding(horizontal = 8.dp).clickable {
                                        openDropdown.value = false
                                        selectedSite.value = it
                                    }
                                )*/
                                Text(it.key, modifier = Modifier.padding(horizontal = 8.dp).clickable {
                                    viewModel.choose(it)
                                    }
                                )
                            }
                        }
                        return@DropdownMenu
                    }
                    viewModel.categories().forEach {
                        DropdownMenuItem(
                            onClick = {
                                viewModel.choose(it)
                            }
                        ) {
                            /*Image(
                                painterResource(it.iconPath()),
                                contentDescription = it.siteName,
                                modifier = Modifier.size(48.dp).padding(8.dp)
                            )*/
                            Text(it.key, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }

            if (viewModel.requireSecondInput()) {
                InputTextField(
                    viewModel.keyword(),
                    "Keyword",
                    onValueChange = {
                        viewModel.onKeywordValueChange(it)
                    },
                    onSearch = {
                        viewModel.onSearch()
                    },
                    clearButton = {
                        viewModel.clearKeywordInput()
                    },
                    viewModel.shouldShowKeywordHistory(),
                    suggestions = viewModel.keywordHistories(),
                    suggestionConsumer = {
                        viewModel.putKeyword(it)
                    },
                    { viewModel.deleteInputHistoryItem(it) },
                    {
                        viewModel.clearKeywordHistory()
                    },
                    modifier = viewModel.focusingModifier()
                )
            }

            InputTextField(
                viewModel.dateInput(),
                "Article name filter",
                onValueChange = {
                    viewModel.onDateInputValueChange(it)
                },
                onSearch = {
                    viewModel.onSearch()
                },
                clearButton = viewModel::clearDateInput,
                viewModel.shouldShowDateHistory(),
                suggestions = viewModel.dateHistories(),
                suggestionConsumer = {
                    viewModel.putDate(it)
                },
                { viewModel.deleteDateHistoryItem(it) },
                {
                    viewModel.clearDateHistory()
                },
                modifier = viewModel.dateInputModifier()
            )

            Button(
                onClick = viewModel::onSearch
            ) {
                Text("Start")
            }
        }
    }

    LaunchedEffect(viewModel.showAggregationBox()) {
        viewModel.start()
    }
}