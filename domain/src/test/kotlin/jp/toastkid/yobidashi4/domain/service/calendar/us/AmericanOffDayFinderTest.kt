/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar.us

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AmericanOffDayFinderTest {

    private lateinit var subject: AmericanOffDayFinder

    @BeforeEach
    fun setUp() {
        subject = AmericanOffDayFinder()
    }

    @Test
    fun invoke() {
        assertEquals(2, subject.invoke(2025, 1, false).size)
        assertEquals(1, subject.invoke(2025, 2, false).size)
        assertEquals(0, subject.invoke(2025, 3, false).size)
        assertEquals(0, subject.invoke(2025, 4, false).size)
        assertEquals(1, subject.invoke(2025, 5, false).size)
        assertEquals(1, subject.invoke(2025, 6, false).size)
        assertEquals(0, subject.invoke(2020, 6, false).size)
        assertEquals(1, subject.invoke(2025, 7, false).size)
        assertEquals(2, subject.invoke(2021, 7, false).size)
        assertEquals(0, subject.invoke(2025, 8, false).size)
        assertEquals(1, subject.invoke(2025, 9, false).size)
    }

}