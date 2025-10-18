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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AmericanOffDayFinderTest {

    private lateinit var subject: AmericanOffDayFinder

    @BeforeEach
    fun setUp() {
        subject = AmericanOffDayFinder()
    }

    @ParameterizedTest
    @CsvSource(
        "2025, 1, 2",
        "2025, 2, 1",
        "2025, 3, 0",
        "2025, 4, 0",
        "2025, 5, 1",
        "2025, 6, 1",
        "2020, 6, 0",
        "2025, 7, 1",
        "2021, 7, 2",
        "2025, 8, 0",
        "2025, 9, 1",
        "2025, 10, 1",
        "2025, 11, 2",
        "2025, 12, 1",
    )
    fun invoke(year: Int, month: Int, expected: Int) {
        assertEquals(expected, subject.invoke(year, month, false).size)
    }

}