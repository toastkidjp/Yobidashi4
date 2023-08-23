package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine

@Composable
fun TableLineView(line: TableLine) {
    Column {
        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.surface)) {
            line.header.forEachIndexed { index, item ->
                if (index != 0) {
                    Divider(modifier = Modifier.height(24.dp).width(1.dp).padding(vertical = 1.dp))
                }

                Text(
                    item.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
            }
        }

        Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))

        line.table.forEach { itemRow ->
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    itemRow.forEachIndexed { index, any ->
                        if (index != 0) {
                            Divider(
                                modifier = Modifier.height(24.dp).width(1.dp)
                                    .padding(vertical = 1.dp)
                            )
                        }
                        Text(
                            any.toString(),
                            fontSize = 24.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
                Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
            }

        }
    }
}
