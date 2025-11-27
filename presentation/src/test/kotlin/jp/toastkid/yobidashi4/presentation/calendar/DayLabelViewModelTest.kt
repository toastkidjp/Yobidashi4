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
import org.junit.jupiter.api.Assertions.assertTrue
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

    @Test
    fun makeText() {
        assertTrue(subject.makeText(-1).isEmpty())
        assertEquals("1", subject.makeText(1))
        assertEquals("31", subject.makeText(31))
    }

    @Test
    fun textColor() {
        assertNotNull(subject.textColor(DayOfWeek.SUNDAY, true, true))
        assertNotNull(subject.textColor(DayOfWeek.SUNDAY, false, true))
        assertNotNull(subject.textColor(DayOfWeek.SUNDAY, false, false))
        assertNotNull(subject.textColor(DayOfWeek.SUNDAY, true, false))
        assertNotNull(subject.textColor(DayOfWeek.SATURDAY, true, true))
        assertNotNull(subject.textColor(DayOfWeek.SATURDAY, false, true))
        assertNotNull(subject.textColor(DayOfWeek.SATURDAY, false, false))
        assertNotNull(subject.textColor(DayOfWeek.SATURDAY, true, false))
        assertNotNull(subject.textColor(DayOfWeek.MONDAY, true, true))
        assertNotNull(subject.textColor(DayOfWeek.MONDAY, true, false))
        assertNotNull(subject.textColor(DayOfWeek.MONDAY, false, true))
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