package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek

@Composable
fun DayLabelView(
    date: Int,
    dayOfWeek: DayOfWeek,
    label: String?,
    offDay: Boolean,
    today: Boolean,
    modifier: Modifier
) {
    val viewModel = remember { DayLabelViewModel() }

    Surface(
        color = if (today) MaterialTheme.colors.primary.copy(alpha = 0.5f)
        else if (viewModel.useOffDayBackground(offDay, dayOfWeek)) Color.White.copy(alpha = 0.8f)
        else MaterialTheme.colors.surface,
        modifier = modifier
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(vertical = 16.dp)
        ) {
            Text(
                viewModel.makeText(date),
                fontSize = 16.sp,
                color = viewModel.textColor(dayOfWeek, offDay, today) ?: MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(label ?: "", fontSize = viewModel.labelSize(label), color = viewModel.labelColor())
        }
    }
}