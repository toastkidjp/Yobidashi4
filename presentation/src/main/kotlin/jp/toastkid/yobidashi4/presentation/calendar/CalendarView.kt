/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.tab.CalendarTab
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightDropdownMenuItem
import jp.toastkid.yobidashi4.presentation.component.SingleLineTextField
import java.time.DayOfWeek
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

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
                calendarViewModel.japaneseYear(),
                calendarViewModel.localDate().month.value,
                calendarViewModel.openingMonthChooser(),
                calendarViewModel::openMonthChooser,
                calendarViewModel::closeMonthChooser,
                calendarViewModel::setYearInput,
                calendarViewModel::plusMonths,
                calendarViewModel::moveToCurrentMonth,
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
                                    DayOfWeek.SATURDAY -> Color(165, 160, 255)
                                    else -> MaterialTheme.colors.onSurface
                                }
                            )
                        }
                    }
                }
            }

            calendarViewModel.month().forEach { w ->
                Row(modifier = Modifier.weight(1f)) {
                    w.days().forEach { day ->
                        val backgroundColor = if (calendarViewModel.isToday(day.date)) MaterialTheme.colors.primary.copy(alpha = 0.5f)
                        //else if (viewModel.useOffDayBackground(offDay, dayOfWeek)) Color.White.copy(alpha = 0.8f)
                        else MaterialTheme.colors.surface
                        DayLabelView(day.date, day.dayOfWeek, day.label, day.offDay,
                            calendarViewModel.isToday(day.date),
                            modifier = Modifier.weight(1f)
                                .fillMaxHeight()
                                .drawBehind {
                                    drawRect(backgroundColor)
                                }
                                .combinedClickable(
                                    enabled = day.date != -1,
                                    onClick = {
                                        calendarViewModel.openDateArticle(day.date)
                                    },
                                    onLongClick = {
                                        calendarViewModel.openDateArticle(day.date, true)
                                    }
                                )
                                .semantics { contentDescription = "day_label_${day.date}" }
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
private fun TopComponent(
    yearInput: TextFieldValue,
    japaneseYear: String,
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
            SingleLineTextField(
                yearInput,
                "Year",
                setYearInput,
                modifier = Modifier.widthIn(60.dp)
            )
        }

        Text(
            japaneseYear,
            modifier = Modifier.widthIn(60.dp)
        )

        Text("/", fontSize = 16.sp, modifier = Modifier.padding(8.dp))

        Surface(modifier = Modifier.padding(8.dp)) {
            Box(modifier = Modifier.clickable(onClick = openMonthChooser)) {
                Text("$currentMonth", fontSize = 16.sp)
                DropdownMenu(expanded = openingMonthChooser, onDismissRequest = closeMonthChooser) {
                    Month.entries.forEach {
                        HoverHighlightDropdownMenuItem(
                            "${it.value}",
                            modifier = Modifier.semantics { contentDescription = "month_chooser_button_${it.value}" }
                        ) {
                            moveMonth(it.value)
                            closeMonthChooser()
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                plusMonths(1)
            },
            modifier = Modifier.padding(8.dp).onPreviewKeyEvent { it.key == Key.DirectionRight }
        ) {
            Text(">", modifier = Modifier.padding(8.dp))
        }

        Button(onClick = moveToCurrentMonth, modifier = Modifier.padding(8.dp)) {
            Text("Current month", modifier = Modifier.padding(8.dp))
        }
    }
}
