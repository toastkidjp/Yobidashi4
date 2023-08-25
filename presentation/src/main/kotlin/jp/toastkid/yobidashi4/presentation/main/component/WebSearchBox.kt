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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
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
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite
import jp.toastkid.yobidashi4.domain.service.tool.calculator.SimpleCalculator
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun WebSearchBox() {
    val viewModel = remember { object : KoinComponent { val vm: MainViewModel by inject() }.vm }
    val focusRequester = remember { FocusRequester() }
    Surface(
        modifier = Modifier.wrapContentHeight().fillMaxWidth(),
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        val selectedSite = remember { mutableStateOf(SearchSite.getDefault()) }
        val query = remember { mutableStateOf(TextFieldValue())}
        val openDropdown = remember { mutableStateOf(false) }

        val calculator = remember { SimpleCalculator() }
        val result = remember { mutableStateOf("") }

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
            )

            Box(
                modifier = Modifier.clickable { openDropdown.value = true }
            ) {
                Surface(elevation = 4.dp) {
                    Image(
                        painterResource(selectedSite.value.iconPath()),
                        contentDescription = selectedSite.value.siteName,
                        modifier = Modifier.size(64.dp).padding(8.dp)
                    )
                }

                val swingContent = containsSwingContent(viewModel.currentTab())
                DropdownMenu(
                    expanded = openDropdown.value,
                    offset = DpOffset(0.dp, if (swingContent) (-80).dp else 0.dp),
                    onDismissRequest = { openDropdown.value = false }
                ) {
                    if (swingContent) {
                        LazyRow(modifier = Modifier.width(300.dp).height(60.dp)) {
                            items(SearchSite.values()) {
                                Image(
                                    painterResource(it.iconPath()),
                                    contentDescription = it.siteName,
                                    modifier = Modifier.size(48.dp).padding(horizontal = 8.dp).clickable {
                                        openDropdown.value = false
                                        selectedSite.value = it
                                    }
                                )
                            }
                        }
                        return@DropdownMenu
                    }
                    SearchSite.values().forEach {
                        DropdownMenuItem(
                            onClick = {
                                openDropdown.value = false
                                selectedSite.value = it
                            }
                        ) {
                            Image(
                                painterResource(it.iconPath()),
                                contentDescription = it.siteName,
                                modifier = Modifier.size(48.dp).padding(8.dp)
                            )
                            Text(it.siteName, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
            TextField(
                query.value,
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent, cursorColor = MaterialTheme.colors.secondary),
                label = { Text("Please would you input web search keyword?", color = MaterialTheme.colors.secondary) },
                onValueChange = {
                    query.value = TextFieldValue(it.text, it.selection, it.composition)
                    val calculatorResult = calculator.invoke(query.value.text)
                    val toString = calculatorResult?.toString()
                    result.value = when {
                        toString == null -> ""
                        toString.endsWith(".0") -> toString.substring(0, toString.lastIndexOf("."))
                        else -> toString
                    }
                },
                keyboardActions = KeyboardActions(
                    onSearch = {
                        selectedSite.value.make(query.value.text).let {
                            viewModel.openUrl(it.toString(), false)
                        }
                        viewModel.setShowWebSearch(false)
                    }
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                trailingIcon = {
                    Icon(
                        painterResource("images/icon/ic_clear_form.xml"),
                        contentDescription = "Clear input.",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.clickable {
                            query.value = TextFieldValue()
                        }
                    )
                },
                modifier = Modifier.focusRequester(focusRequester)
                    .onKeyEvent {
                        if (it.type == KeyEventType.KeyDown && it.key == Key.Enter
                            && query.value.composition == null) {
                            if (query.value.text.startsWith("https://")) {
                                viewModel.openUrl(query.value.text, false)
                                return@onKeyEvent true
                            }
                            selectedSite.value.make(query.value.text).let { uri ->
                                viewModel.openUrl(uri.toString(), false)
                            }
                            viewModel.setShowWebSearch(false)
                        }
                        true
                    }
            )

            Button(
                onClick = {
                    if (query.value.text.startsWith("https://")) {
                        viewModel.openUrl(query.value.text, false)
                        return@Button
                    }
                    selectedSite.value.make(query.value.text).let {
                        viewModel.openUrl(it.toString(), false)
                    }
                    viewModel.setShowWebSearch(false)
                }
            ) {
                Text("Search")
            }

            if (result.value.isNotBlank()) {
                SelectionContainer {
                    Text(result.value)
                }
            }

            LaunchedEffect(viewModel.showWebSearch()) {
                if (viewModel.showWebSearch()) {
                    focusRequester.requestFocus()
                }
                query.value = TextFieldValue(
                    (viewModel.currentTab() as? WebTab)?.url() ?: ""
                )
            }
        }
    }
}

private fun containsSwingContent(currentTab: Tab?): Boolean {
    val swingContent = currentTab is WebTab || currentTab is EditorTab
    return swingContent
}