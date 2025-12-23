/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EquinoxDayCalculatorTest {

    private lateinit var equinoxDayCalculator: EquinoxDayCalculator

    @BeforeEach
    fun setUp() {
        equinoxDayCalculator = EquinoxDayCalculator()
    }

    @Test
    fun test() {
        assertEquals(20, equinoxDayCalculator.calculateVernalEquinoxDay(2020)?.day)
        assertEquals(22, equinoxDayCalculator.calculateAutumnalEquinoxDay(2020)?.day)
    }

    @Test
    fun case224() {
        assertNull(equinoxDayCalculator.calculateVernalEquinoxDay(224))
        assertNull(equinoxDayCalculator.calculateAutumnalEquinoxDay(224))
    }

    @Test
    fun over3000() {
        assertNull(equinoxDayCalculator.calculateVernalEquinoxDay(20124))
        assertNull(equinoxDayCalculator.calculateAutumnalEquinoxDay(20124))
    }

}