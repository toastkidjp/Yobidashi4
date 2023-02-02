package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek

@Composable
fun DayLabelView(date: Int, dayOfWeek: DayOfWeek, offDay: Boolean, today: Boolean, modifier: Modifier) {
    Surface(
        color = if (today) MaterialTheme.colors.primary.copy(alpha = 0.5f) else if (offDay || dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) Color.White.copy(alpha = 0.8f) else MaterialTheme.colors.surface,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(if (date == -1) "" else "$date", fontSize = 16.sp, color = when (dayOfWeek) {
                DayOfWeek.SUNDAY -> OFF_DAY_FG
                DayOfWeek.SATURDAY -> SATURDAY_FG
                else -> if (offDay) OFF_DAY_FG else if (today) Color.White else MaterialTheme.colors.onSurface
            })
        }
    }
}

private val DAY_FG: Color = Color.Black
private val OFF_DAY_FG:  Color = Color(220, 50, 55)
private val SATURDAY_FG:  Color = Color(55, 50, 190)
private val DAY_BG: Color = Color(250, 250, 255)