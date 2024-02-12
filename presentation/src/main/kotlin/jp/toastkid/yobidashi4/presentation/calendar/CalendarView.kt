package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarView(tab: CalendarTab) {
    val calendarViewModel = remember { CalendarViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Column {
            TopComponent(
                calendarViewModel.yearInput(),
                calendarViewModel.localDate().month.value,
                calendarViewModel.openingMonthChooser(),
                calendarViewModel::openMonthChooser,
                calendarViewModel::closeMonthChooser,
                { calendarViewModel.setYearInput(it) },
                { calendarViewModel.plusMonths(it) },
                { calendarViewModel.moveToCurrentMonth() },
                calendarViewModel::moveMonth
            )
            Row {
                calendarViewModel.dayOfWeeks().forEach { dayOfWeek ->
                    Surface(modifier = Modifier.weight(1f)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (dayOfWeek) {
                                    DayOfWeek.SUNDAY -> Color(190, 50, 55)
                                    DayOfWeek.SATURDAY -> SATURDAY_FG
                                    else -> MaterialTheme.colors.onSurface
                                }
                            )
                        }
                    }
                }
            }

            calendarViewModel.month().forEach { w ->
                Row {
                    w.days().forEach { day ->
                        DayLabelView(day.date, day.dayOfWeek, day.label, day.offDay,
                            calendarViewModel.isToday(day.date),
                            modifier = Modifier.weight(1f)
                                .combinedClickable(
                                    enabled = day.date != -1,
                                    onClick = {
                                        calendarViewModel.openDateArticle(day.date)
                                    },
                                    onLongClick = {
                                        calendarViewModel.openDateArticle(day.date, true)
                                    }
                                )
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(tab) {
        calendarViewModel.launch(tab.localDate())

        onDispose {
            calendarViewModel.onDispose(tab)
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun TopComponent(
    yearInput: TextFieldValue,
    currentMonth: Int,
    openingMonthChooser: Boolean,
    openMonthChooser: () -> Unit,
    closeMonthChooser: () -> Unit,
    setYearInput: (TextFieldValue) -> Unit,
    plusMonths: (Long) -> Unit,
    moveToCurrentMonth: () -> Unit,
    moveMonth: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(8.dp)
    ) {
        Button(onClick = {
            plusMonths(-1)
        }, modifier = Modifier.padding(8.dp).onPreviewKeyEvent { it.key == Key.DirectionLeft }) {
            Text("<", modifier = Modifier.padding(8.dp))
        }

        Surface(modifier = Modifier.padding(8.dp)) {
            TextField(
                yearInput,
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                label = { Text("Installment") },
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
                onValueChange = {
                    setYearInput(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.widthIn(100.dp)
            )
        }

        Text("/", fontSize = 16.sp, modifier = Modifier.padding(8.dp))

        Surface(modifier = Modifier.padding(8.dp)) {
            Box(modifier = Modifier.clickable { openMonthChooser() }) {
                Text("$currentMonth", fontSize = 16.sp)
                DropdownMenu(expanded = openingMonthChooser, onDismissRequest = { closeMonthChooser() }) {
                    Month.values().forEach {
                        DropdownMenuItem(onClick = {
                            moveMonth(it.value)
                            closeMonthChooser()
                        }) {
                            Text("${it.value}")
                        }
                    }
                }
            }
        }

        Button(onClick = {
            plusMonths(1)
        }, modifier = Modifier.padding(8.dp).onPreviewKeyEvent { it.key == Key.DirectionRight }) {
            Text(">", modifier = Modifier.padding(8.dp))
        }

        Button(onClick = {
            moveToCurrentMonth()
        }, modifier = Modifier.padding(8.dp).onPreviewKeyEvent { it.key == Key.DirectionLeft }) {
            Text("Current month", modifier = Modifier.padding(8.dp))
        }
    }
}

private val SATURDAY_FG: Color = Color(55, 50, 190)
