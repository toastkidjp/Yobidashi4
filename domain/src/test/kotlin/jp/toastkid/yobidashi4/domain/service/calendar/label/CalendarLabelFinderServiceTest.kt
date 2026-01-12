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
import org.junit.jupiter.api.Test

class CalendarLabelFinderServiceTest {

    private lateinit var subject: CalendarLabelFinderService

    @BeforeEach
    fun setUp() {
        subject = CalendarLabelFinderService()
    }

    @Test
    fun invoke() {
        val labels2023Dec = subject.invoke(2023, 12)
        assertEquals(29, labels2023Dec.first { it.title == "大納会" }.day)
        val labels2024Dec = subject.invoke(2024, 12)
        assertEquals(30, labels2024Dec.first { it.title == "大納会" }.day)
        println(subject.invoke(2025, 1))
        println(subject.invoke(2025, 3))
        val labels2025Nov = subject.invoke(2025, 11)
        assertEquals(2, labels2025Nov.first { it.title == "冬時間入り" }.day)
    }

}