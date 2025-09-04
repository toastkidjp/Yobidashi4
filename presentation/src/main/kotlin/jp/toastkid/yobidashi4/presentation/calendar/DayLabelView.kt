package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightColumn
import java.time.DayOfWeek

@Composable
fun DayLabelView(
    date: Int,
    dayOfWeek: DayOfWeek,
    label: List<String>,
    offDay: Boolean,
    today: Boolean,
    modifier: Modifier
) {
    val viewModel = remember { DayLabelViewModel() }

    HoverHighlightColumn(
        modifier = modifier
    ) { textColor ->
        Text(
            viewModel.makeText(date),
            fontSize = 16.sp,
            color = viewModel.textColor(dayOfWeek, offDay, today) ?: textColor,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        label.forEach { text ->
            Text(text, fontSize = viewModel.labelSize(text), color = viewModel.labelColor())
        }
    }
}