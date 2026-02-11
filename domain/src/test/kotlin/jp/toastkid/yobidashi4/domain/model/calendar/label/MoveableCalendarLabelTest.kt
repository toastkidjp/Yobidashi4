/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar.label

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.fail

class MoveableCalendarLabelTest {

    @Test
    fun find() {
        assertNull(MoveableCalendarLabel.START_DAY_OF_DAYLIGHT_SAVING_TIME.find(2000, 1))
        val startDayOf2020Mar = MoveableCalendarLabel.START_DAY_OF_DAYLIGHT_SAVING_TIME.find(2020, 3) ?: fail("")
        assertEquals(3, startDayOf2020Mar.month)
        assertEquals(1, startDayOf2020Mar.day)

        val startDayOf2020Nov = MoveableCalendarLabel.END_DAY_OF_DAYLIGHT_SAVING_TIME.find(2020, 11) ?: fail("")
        assertEquals(11, startDayOf2020Nov.month)
        assertEquals(1, startDayOf2020Nov.day)
    }

}