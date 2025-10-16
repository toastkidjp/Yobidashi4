/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar.uk

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UKOffDayFinderTest {

    private lateinit var subject: UKOffDayFinder

    @BeforeEach
    fun setUp() {
        subject = UKOffDayFinder()
    }

    @Test
    fun invoke() {
        assertEquals(1, subject.invoke(2025, 1).size)
        assertTrue(subject.invoke(2025, 2).isEmpty())
        assertTrue(subject.invoke(2025, 3).isEmpty())
        assertEquals(2, subject.invoke(2025, 4).size)
        assertEquals(2, subject.invoke(2025, 5).size)
        assertEquals(0, subject.invoke(2025, 6).size)
        assertEquals(0, subject.invoke(2025, 7).size)
        assertEquals(1, subject.invoke(2025, 8).size)
        assertEquals(0, subject.invoke(2025, 9).size)
        assertEquals(0, subject.invoke(2025, 10).size)
        assertEquals(0, subject.invoke(2025, 11).size)
        assertEquals(2, subject.invoke(2025, 12).size)
    }

}