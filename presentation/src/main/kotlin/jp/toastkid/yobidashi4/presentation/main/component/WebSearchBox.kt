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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite

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
            )

            Box(
                modifier = Modifier.clickable { viewModel.setOpenDropdown() }
            ) {
                Surface(elevation = 4.dp) {
                    Image(
                        painterResource(viewModel.currentIconPath()),
                        contentDescription = viewModel.currentSiteName(),
                        modifier = Modifier.size(64.dp).padding(8.dp)
                    )
                }

                val swingContent = viewModel.containsSwingContent()
                DropdownMenu(
                    expanded = viewModel.openingDropdown(),
                    offset = DpOffset(0.dp, if (swingContent) (-80).dp else 0.dp),
                    onDismissRequest = { viewModel.closeDropdown() }
                ) {
                    if (swingContent) {
                        LazyRow(modifier = Modifier.width(600.dp).height(60.dp)) {
                            items(SearchSite.values()) {
                                Image(
                                    painterResource(it.iconPath()),
                                    contentDescription = it.siteName,
                                    modifier = Modifier.size(48.dp).padding(horizontal = 8.dp).clickable {
                                        viewModel.choose(it)
                                    }
                                )
                            }
                        }
                        return@DropdownMenu
                    }
                    SearchSite.values().forEach {
                        DropdownMenuItem(
                            onClick = {
                                viewModel.choose(it)
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
            Box {
                TextField(
                    viewModel.query(),
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent, cursorColor = MaterialTheme.colors.secondary),
                    label = { Text("Please would you input web search keyword?", color = MaterialTheme.colors.secondary) },
                    onValueChange = {
                        viewModel.onValueChange(it)
                    },
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.invokeSearch()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    visualTransformation = {
                        TransformedText(AnnotatedString(it.replace("\n".toRegex(), " ")), OffsetMapping.Identity)
                    },
                    trailingIcon = {
                        Icon(
                            painterResource("images/icon/ic_clear_form.xml"),
                            contentDescription = "Clear input.",
                            tint = MaterialTheme.colors.secondary,
                            modifier = Modifier.clickable {
                                viewModel.clearInput()
                            }
                        )
                    },
                    modifier = Modifier.focusRequester(viewModel.focusRequester())
                        .onKeyEvent {
                            viewModel.onKeyEvent(it)
                        }
                )

                DropdownMenu(
                    viewModel.shouldShowInputHistory(),
                    properties = PopupProperties(clippingEnabled = false),
                    onDismissRequest = {  }
                ) {
                    viewModel.inputHistories().forEach {
                        DropdownMenuItem({
                            viewModel.putText(it.word)
                        }) {
                            Text(it.word)
                        }
                    }
                }
            }

            Button(
                onClick = viewModel::invokeSearch
            ) {
                Text("Search")
            }

            if (viewModel.existsResult()) {
                SelectionContainer {
                    Text(viewModel.result())
                }
            }

            LaunchedEffect(viewModel.showWebSearch()) {
                viewModel.start()
            }
        }
    }
}
