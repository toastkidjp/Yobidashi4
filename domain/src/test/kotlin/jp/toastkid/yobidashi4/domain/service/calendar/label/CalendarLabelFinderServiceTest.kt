/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar.label

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CalendarLabelFinderServiceTest {

    private lateinit var subject: CalendarLabelFinderService

    @BeforeEach
    fun setUp() {
        subject = CalendarLabelFinderService()
    }

    @ParameterizedTest
    @CsvSource(
        "2018, 12, 28, 大納会",
        "2023, 12, 29, 大納会",
        "2024, 12, 30, 大納会",
        "2018, 1, 4, 大発会",
        "2026, 1, 5, 大発会",
        "2025, 1, 6, 大発会",
        "2025, 3, 9, 夏時間入り",
        "2025, 11, 2, 冬時間入り",
        )
    fun invoke(year: Int, month: Int, date: Int, title: String) {
        val labels2018Dec = subject.invoke(year, month)
        assertEquals(date, labels2018Dec.first { it.title == title }.day)
    }

}