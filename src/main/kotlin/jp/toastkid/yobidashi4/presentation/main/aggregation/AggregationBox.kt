package jp.toastkid.yobidashi4.presentation.main.aggregation

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.service.aggregation.ArticleLengthAggregatorService
import jp.toastkid.yobidashi4.domain.service.aggregation.EatingOutCounterService
import jp.toastkid.yobidashi4.domain.service.aggregation.MovieMemoSubtitleExtractor
import jp.toastkid.yobidashi4.domain.service.aggregation.Nikkei225AggregatorService
import jp.toastkid.yobidashi4.domain.service.aggregation.OutgoAggregatorService
import jp.toastkid.yobidashi4.domain.service.aggregation.StepsAggregatorService
import jp.toastkid.yobidashi4.domain.service.aggregation.StocksAggregatorService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun AggregationBox(viewModel: MainViewModel) {
    val focusRequester = remember { FocusRequester() }
    Surface(
        modifier = Modifier.wrapContentHeight().fillMaxWidth(),
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        val aggregations = remember {
            mapOf<String, (String) -> AggregationResult>(
                "Movies" to { MovieMemoSubtitleExtractor().invoke(it) },
                "Stock" to { StocksAggregatorService().invoke(it) },
                "Outgo" to { OutgoAggregatorService().invoke(it) },
                "Eat out" to { EatingOutCounterService().invoke(it) },
                "Article length" to { ArticleLengthAggregatorService().invoke(it) },
                "Steps" to { StepsAggregatorService().invoke(it) },
                "Nikkei 225" to { Nikkei225AggregatorService().invoke(it) }
            )
        }
        val selectedSite = remember { mutableStateOf(aggregations.entries.first()) }
        val query = remember { mutableStateOf(TextFieldValue("${LocalDate.now().year}")) }
        val openDropdown = remember { mutableStateOf(false) }
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
                modifier = Modifier.clickable { openDropdown.value = true }
            ) {
                Surface(elevation = 4.dp) {
                    /*Image(
                        painterResource(selectedSite.value.iconPath()),
                        contentDescription = selectedSite.value.siteName,
                        modifier = Modifier.size(64.dp).padding(8.dp)
                    )*/
                    Text(selectedSite.value.key, modifier = Modifier.padding(start = 8.dp))
                }

                val currentTab = if (viewModel.tabs.isEmpty()) null else viewModel.tabs[viewModel.selected.value]
                val swingContent = currentTab is WebTab || currentTab is EditorTab
                DropdownMenu(
                    expanded = openDropdown.value,
                    offset = DpOffset(0.dp, if (swingContent) (-80).dp else 0.dp),
                    onDismissRequest = { openDropdown.value = false }
                ) {
                    if (swingContent) {
                        LazyRow(modifier = Modifier.width(300.dp).height(60.dp)) {
                            items(aggregations.entries.toList()) {
                               /* Image(
                                    painterResource(it.iconPath()),
                                    contentDescription = it.siteName,
                                    modifier = Modifier.size(48.dp).padding(horizontal = 8.dp).clickable {
                                        openDropdown.value = false
                                        selectedSite.value = it
                                    }
                                )*/
                                Text(it.key, modifier = Modifier.padding(horizontal = 8.dp).clickable {
                                        openDropdown.value = false
                                        selectedSite.value = it
                                    }
                                )
                            }
                        }
                        return@DropdownMenu
                    }
                    aggregations.forEach {
                        DropdownMenuItem(
                            onClick = {
                                openDropdown.value = false
                                selectedSite.value = it
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
            TextField(
                query.value,
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                label = { Text("Please would you input web search keyword?") },
                onValueChange = {
                    query.value = TextFieldValue(it.text, it.selection, it.composition)
                },
                keyboardActions = KeyboardActions(
                    onSearch = {
                        selectedSite.value.value(query.value.text)
                        viewModel.setShowWebSearch(false)
                    }
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                trailingIcon = {
                    Icon(
                        painterResource("images/icon/ic_clear_form.xml"),
                        contentDescription = "Clear input.",
                        tint = MaterialTheme.colors.primary,
                        modifier = Modifier.clickable {
                            query.value = TextFieldValue()
                        }
                    )
                },
                modifier = Modifier.focusRequester(focusRequester)
                    .onKeyEvent {
                        if (it.type == KeyEventType.KeyDown && it.key == Key.Enter
                            && query.value.composition == null
                            && query.value.text.isNotBlank()
                            ) {
                            selectedSite.value.value(query.value.text).let {
                                viewModel.openAggregationResultTab(it.resultTitleSuffix(), it)
                            }
                            viewModel.setShowWebSearch(false)
                        }
                        true
                    }
            )

            Button(
                onClick = {
                    if (query.value.text.isBlank()) {
                        return@Button
                    }
                    selectedSite.value.value(query.value.text).let {
                        viewModel.openAggregationResultTab(it.resultTitleSuffix(), it)
                    }
                    viewModel.setShowWebSearch(false)
                }
            ) {
                Text("Start")
            }

            LaunchedEffect(viewModel.showWebSearch()) {
                if (viewModel.showWebSearch()) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}