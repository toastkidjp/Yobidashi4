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
import androidx.compose.runtime.MutableState
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

@Composable
fun TableLineView(line: TableLine, fontSize: TextUnit = 24.sp, modifier: Modifier = Modifier) {
    var lastSorted = remember { -1 to false }

    val tableData = remember { mutableStateOf(line.table) }

    Column(modifier = modifier) {
        DisableSelection {
            Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.surface)) {
                line.header.forEachIndexed { index, item ->
                    if (index != 0) {
                        VerticalDivider(modifier = Modifier.height(24.dp).padding(vertical = 1.dp))
                    }

                    Text(
                        item.toString(),
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .clickable {
                                val lastSortOrder = if (lastSorted.first == index) lastSorted.second else false
                                lastSorted = index to lastSortOrder.not()

                                sort(lastSortOrder, index, tableData)
                            }
                    )
                }
            }
        }

        Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))

        tableData.value.forEach { itemRow ->
            TableRow(itemRow, fontSize)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TableRow(itemRow: List<Any>, fontSize: TextUnit) {
    Column {
        val cursorOn = remember { mutableStateOf(false) }
        val backgroundColor = animateColorAsState(if (cursorOn.value) MaterialTheme.colors.primary else Color.Transparent)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.drawBehind { drawRect(backgroundColor.value) }
            .onPointerEvent(PointerEventType.Enter) {
                cursorOn.value = true
            }
            .onPointerEvent(PointerEventType.Exit) {
                cursorOn.value = false
            }
        ) {
            itemRow.forEachIndexed { index, any ->
                if (index != 0) {
                    VerticalDivider(modifier = Modifier.height(24.dp).padding(vertical = 1.dp))
                }
                Text(
                    any.toString(),
                    color = if (cursorOn.value) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
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

private fun sort(
    lastSortOrder: Boolean,
    index: Int,
    articleStates: MutableState<List<List<Any>>>
) {
    val first = articleStates.value.firstOrNull() ?: return
    val snapshot = articleStates.value
    val swap = if (lastSortOrder)
        if (first[index].toString().toDoubleOrNull() != null) {
            snapshot.sortedBy { it[index].toString().toDoubleOrNull() ?: 0.0 }
        } else if (first[index].toString().toIntOrNull() != null) {
            snapshot.sortedBy { it[index].toString().toIntOrNull() ?: 0 }
        } else {
            snapshot.sortedBy { it[index].toString() }
        }
    else
        if (first[index].toString().toDoubleOrNull() != null) {
            snapshot.sortedByDescending { it[index].toString().toDoubleOrNull() ?: 0.0 }
        } else if (first[index].toString().toIntOrNull() != null) {
            snapshot.sortedByDescending { it[index].toString().toIntOrNull() ?: 0 }
        } else {
            snapshot.sortedByDescending { it[index].toString() }
        }

    articleStates.value = swap
}
