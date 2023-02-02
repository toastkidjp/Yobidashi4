package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import jp.toastkid.yobidashi4.domain.model.calendar.Week
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.article.ArticleTitleGenerator
import jp.toastkid.yobidashi4.presentation.viewmodel.calendar.CalendarViewModel
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun CalendarView() {
    val calendarViewModel = object : KoinComponent { val vm: CalendarViewModel by inject() }.vm

    val week = arrayOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(8.dp)) {
                Button(onClick = {
                    calendarViewModel.plusMonths(-1)
                }, modifier = Modifier.padding(8.dp).onPreviewKeyEvent { it.key ==  Key.DirectionLeft }) {
                    Text("<", modifier = Modifier.padding(8.dp))
                }

                Surface(modifier = Modifier.padding(8.dp)) {
                    val openYearChooser = remember { mutableStateOf(false) }
                    Box(modifier = Modifier.clickable { openYearChooser.value = true }) {
                        Text("${calendarViewModel.localDate().year}", fontSize = 16.sp)
                        DropdownMenu(
                            expanded = openYearChooser.value,
                            onDismissRequest = { openYearChooser.value = false }) {
                            val years = (1900..2200).toList()
                            Box {
                                val state = rememberLazyListState(years.indexOf(calendarViewModel.localDate().year))
                                LazyColumn(state = state, modifier = Modifier.size(200.dp, 500.dp)) {
                                    items(years) {
                                        Text("${it}", fontSize = 16.sp, modifier = Modifier.padding(8.dp).clickable {
                                            calendarViewModel.setYear(it)
                                            openYearChooser.value = false
                                        })
                                    }
                                }
                                VerticalScrollbar(adapter = rememberScrollbarAdapter(state), modifier = Modifier.height(500.dp).align(Alignment.CenterEnd))
                            }
                        }
                    }
                }

                Text("/", fontSize = 16.sp, modifier = Modifier.padding(8.dp))

                Surface(modifier = Modifier.padding(8.dp)) {
                    val openMonthChooser = remember { mutableStateOf(false) }
                    Box(modifier = Modifier.clickable { openMonthChooser.value = true }) {
                        Text("${calendarViewModel.localDate().month.value}", fontSize = 16.sp)
                        DropdownMenu(expanded = openMonthChooser.value, onDismissRequest = { openMonthChooser.value = false }) {
                            Month.values().forEach {
                                DropdownMenuItem(onClick = {
                                    calendarViewModel.moveMonth(it.value)
                                    openMonthChooser.value = false
                                }) {
                                    Text("${it.value}")
                                }
                            }
                        }
                    }
                }

                Button(onClick = {
                    calendarViewModel.plusMonths(1)
                }, modifier = Modifier.padding(8.dp).onPreviewKeyEvent { it.key ==  Key.DirectionRight }) {
                    Text(">", modifier = Modifier.padding(8.dp))
                }
            }
            Row {
                week.forEach { dayOfWeek ->
                    Surface(modifier = Modifier.weight(1f)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "${dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (dayOfWeek) {
                                    DayOfWeek.SUNDAY -> OFF_DAY_FG
                                    DayOfWeek.SATURDAY -> SATURDAY_FG
                                    else -> MaterialTheme.colors.onSurface
                                }
                            )
                        }
                    }
                }
            }

            val firstDay = calendarViewModel.getFirstDay()

            val weeks = makeMonth(week, firstDay)

            weeks.forEach { w ->
                Row {
                    w.days().forEach { day ->
                        DayLabelView(day.date, day.dayOfWeek, day.offDay,
                            isToday(calendarViewModel.localDate(), day.date),
                            modifier = Modifier.weight(1f)
                                .combinedClickable(
                                    enabled = day.date != -1,
                                    onClick = {
                                        if (day.date == -1) {
                                            return@combinedClickable
                                        }
                                        openDateArticle(calendarViewModel.localDate().withDayOfMonth(day.date))
                                    },
                                    onLongClick = {
                                        if (day.date == -1) {
                                            return@combinedClickable
                                        }
                                        openDateArticle(calendarViewModel.localDate().withDayOfMonth(day.date), true)
                                    }
                                )
                        )
                    }
                }
            }
        }
    }
}

private fun isToday(value: LocalDate, date: Int): Boolean {
    return value.year == LocalDate.now().year && value.month == LocalDate.now().month && LocalDate.now().dayOfMonth == date
}

private fun openDateArticle(localDate: LocalDate, onBackground: Boolean = false) {
    val koin = object : KoinComponent {
        val viewModel: MainViewModel by inject()
        val setting: Setting by inject()
    }
    koin.viewModel.openFile(
        koin.setting.articleFolderPath().resolve(
            "${ArticleTitleGenerator().invoke(localDate)}.md"
        ),
        onBackground
    )
}

private fun makeMonth(
    week: Array<DayOfWeek>,
    firstDay: LocalDate
): MutableList<Week> {
    var hasStarted1 = false
    var current1 = firstDay
    val weeks = mutableListOf<Week>()
    for (i in 0..5) {
        val w = Week()
        week.forEach { dayOfWeek ->
            if (hasStarted1.not() && dayOfWeek != firstDay.dayOfWeek) {
                w.addEmpty()
                return@forEach
            }
            hasStarted1 = true

            if (firstDay.month != current1.month) {
                w.addEmpty()
            } else {
                w.add(current1)
            }
            current1 = current1.plusDays(1L)
        }
        if (w.anyApplicableDate()) {
            weeks.add(w)
        }
    }
    return weeks
}

private val DAY_FG: Color = Color.Black
private val OFF_DAY_FG: Color = Color(190, 50, 55)
private val SATURDAY_FG: Color = Color(55, 50, 190)
