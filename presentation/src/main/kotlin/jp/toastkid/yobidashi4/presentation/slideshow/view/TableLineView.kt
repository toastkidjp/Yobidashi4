package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import jp.toastkid.yobidashi4.presentation.component.VerticalDivider

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TableLineView(line: TableLine, fontSize: TextUnit = 24.sp, modifier: Modifier = Modifier) {
    val viewModel = remember { TableLineViewModel() }

    Column(modifier = modifier) {
        DisableSelection {
            Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.surface)) {
                line.header.forEachIndexed { index, item ->
                    if (index != 0) {
                        VerticalDivider(modifier = Modifier.height(24.dp).padding(vertical = 1.dp))
                    }

                    val headerColumnBackgroundColor = animateColorAsState(
                        if (viewModel.onCursorOnHeader()) MaterialTheme.colors.primary
                        else MaterialTheme.colors.surface
                    )

                    Text(
                        item.toString(),
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.clickHeaderColumn(index)
                            }
                            .onPointerEvent(PointerEventType.Enter) {
                                viewModel.setCursorOnHeader()
                            }
                            .onPointerEvent(PointerEventType.Exit) {
                                viewModel.setCursorOffHeader()
                            }
                            .drawBehind { drawRect(headerColumnBackgroundColor.value) }
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }

        Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))

        viewModel.tableData().forEach { itemRow ->
            val cursorOn = remember { mutableStateOf(false) }
            val backgroundColor = animateColorAsState(if (cursorOn.value) MaterialTheme.colors.primary else Color.Transparent)
            val textColor = if (cursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface

            TableRow(itemRow, fontSize, textColor, Modifier.drawBehind { drawRect(backgroundColor.value) }
                .onPointerEvent(PointerEventType.Enter) {
                    cursorOn.value = true
                }
                .onPointerEvent(PointerEventType.Exit) {
                    cursorOn.value = false
                })
        }
    }

    LaunchedEffect(line.table) {
        viewModel.start(line.table)
    }
}

@Composable
private fun TableRow(itemRow: List<Any>, fontSize: TextUnit, textColor: Color, modifier: Modifier) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            itemRow.forEachIndexed { index, any ->
                if (index != 0) {
                    VerticalDivider(modifier = Modifier.height(24.dp).padding(vertical = 1.dp))
                }
                Text(
                    any.toString(),
                    color = textColor,
                    fontSize = fontSize,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
            }
        }
        Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
    }
}
