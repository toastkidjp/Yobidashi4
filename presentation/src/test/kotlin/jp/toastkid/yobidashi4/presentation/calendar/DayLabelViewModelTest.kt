/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.ui.unit.sp
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.DayOfWeek

class DayLabelViewModelTest {

    @InjectMockKs
    private lateinit var subject: DayLabelViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @ParameterizedTest
    @CsvSource(
        "-1, ''",
        "1, 1",
        "31, 31",
    )
    fun makeText(input: Int, expected: String) {
        assertEquals(expected, subject.makeText(input))
    }

    @ParameterizedTest
    @CsvSource(
        "SUNDAY, true, true",
        "SUNDAY, false, true",
        "SUNDAY, false, false",
        "SUNDAY, true, false",
        "SATURDAY, true, true",
        "SATURDAY, false, true",
        "SATURDAY, false, false",
        "SATURDAY, true, false",
        "MONDAY, true, true",
        "MONDAY, true, false",
        "MONDAY, false, true",
    )
    fun textColor(dayOfWeek: DayOfWeek, offDay: Boolean, today: Boolean) {
        assertNotNull(subject.textColor(dayOfWeek, offDay, today))
        assertNull(subject.textColor(DayOfWeek.MONDAY, false, false))
    }

    @ParameterizedTest
    @CsvSource(
        "null, 12",
        "'', 12",
        "' ', 11",
        "test, 11",
        nullValues = ["null"]
    )
    fun labelSize(input: String?, expected: Int) {
        assertEquals(expected.sp, subject.labelSize(input))
    }

    @Test
    fun labelColor() {
        assertNotNull(subject.labelColor())
    }

    @ParameterizedTest
    @CsvSource(
        "SUNDAY, true, true",
        "MONDAY, true, true",
        "SATURDAY, false, true",
        "SUNDAY, false, true",
        "TUESDAY, false, false",
    )
    fun useOffDayBackground(dayOfWeek: String, offDay: Boolean, expected: Boolean) {
        assertEquals(expected, subject.useOffDayBackground(offDay, DayOfWeek.valueOf(dayOfWeek)))
    }

}