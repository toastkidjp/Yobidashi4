/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar.holiday.us

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull

class FixedAmericanHolidayTest {

    @Test
    fun test() {
        assertNull(FixedAmericanHoliday.find(2020, 6))
        assertNotNull(FixedAmericanHoliday.find(2021, 6))
    }

}