package jp.toastkid.yobidashi4.presentation.time

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightColumn
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightDropdownMenuItem

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun WorldTimeView(modifier: Modifier) {
    val viewModel = remember { WorldTimeViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        modifier = modifier
    ) {
        Box {
            LazyColumn(state = viewModel.listState()) {
                stickyHeader {
                    val backgroundColor = animateColorAsState(
                        if (viewModel.listState().firstVisibleItemIndex != 0) MaterialTheme.colors.surface
                        else Color.Transparent
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().drawBehind { drawRect(backgroundColor.value) }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clickable(onClick = viewModel::openChooser)
                                .padding(horizontal = 8.dp)
                                .semantics { contentDescription = "Timezone chooser" }
                        ) {
                            Text(
                                viewModel.currentTimezoneLabel(),
                            )
                            DropdownMenu(
                                viewModel.openingChooser(),
                                viewModel::closeChooser
                            ) {
                                viewModel.pickupTimeZone().forEach {
                                    HoverHighlightDropdownMenuItem(
                                        viewModel.label(it),
                                        modifier = Modifier.semantics { contentDescription = "Timezone chooser's item ${viewModel.label(it)}" },
                                    ) { viewModel.choose(it) }
                                }
                            }
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.clickable(onClick = viewModel::openHourChooser)
                                .padding(start = 8.dp)
                                .semantics { contentDescription = "Hour chooser" }
                        ) {
                            Text(
                                viewModel.currentHour(),
                                fontSize = 24.sp
                            )
                            DropdownMenu(
                                viewModel.openingHourChooser(),
                                viewModel::closeHourChooser,
                            ) {
                                (0..23).forEach {
                                    HoverHighlightDropdownMenuItem(
                                        "$it",
                                        modifier = Modifier.semantics { contentDescription = "Hour chooser's item $it" },
                                        fontSize = 24.sp
                                    ) { viewModel.chooseHour(it) }
                                }
                            }
                        }

                        Text(
                            ":",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                                .align(Alignment.CenterVertically)
                        )

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.clickable(onClick = viewModel::openMinuteChooser)
                                .semantics { contentDescription = "Minute chooser" }
                        ) {
                            Text(
                                viewModel.currentMinute(),
                                fontSize = 24.sp
                            )
                            DropdownMenu(
                                viewModel.openingMinuteChooser(),
                                viewModel::closeMinuteChooser
                            ) {
                                (0..59).forEach {
                                    HoverHighlightDropdownMenuItem(
                                        "$it",
                                        modifier = Modifier.semantics { contentDescription = "Minute chooser's item $it" },
                                        fontSize = 24.sp
                                    ) {
                                        viewModel.chooseMinute(it)
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = viewModel::setDefault,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text("Default")
                        }
                    }
                }

                items(viewModel.items(), { it }) { item ->
                    val cursorOn = remember { mutableStateOf(false) }

                    HoverHighlightColumn(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                            .animateItem()
                            .fillMaxWidth()
                            .onClick {
                                viewModel.onClickItem(item)
                            }
                            .semantics { contentDescription = item.timeZone() }
                    ) {
                        Text(
                            viewModel.label(item.timeZone()),
                            color = if (cursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            item.time,
                            color = if (cursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                            fontSize = 14.sp
                        )
                    }

                    Divider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.listState()),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }

    SideEffect {
        viewModel.start()
    }
}