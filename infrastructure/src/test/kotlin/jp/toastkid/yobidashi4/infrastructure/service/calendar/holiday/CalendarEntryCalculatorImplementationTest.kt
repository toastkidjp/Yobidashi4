/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.calendar.holiday

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.service.calendar.UserOffDayService
import jp.toastkid.yobidashi4.domain.service.calendar.holiday.CalendarEntryCalculator
import jp.toastkid.yobidashi4.domain.service.calendar.label.CalendarLabelFinderService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CalendarEntryCalculatorImplementationTest {

    private lateinit var subject: CalendarEntryCalculator

    @RelaxedMockK
    private lateinit var calendarLabelFinderService: CalendarLabelFinderService

    @RelaxedMockK
    private lateinit var userOffDayService: UserOffDayService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        subject = CalendarEntryCalculatorImplementation(calendarLabelFinderService, userOffDayService)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        assertEquals(0, subject.invoke(2026, 10, 10).size)
        assertEquals(0, subject.invoke(2026, 10, 11).size)
        assertEquals(2, subject.invoke(2026, 10, 12).size)
        assertEquals(0, subject.invoke(2026, 10, 10).size)
    }

}