/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar.uk

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class UKOffDayFinderTest {

    private lateinit var subject: UKOffDayFinder

    @BeforeEach
    fun setUp() {
        subject = UKOffDayFinder()
    }

    @ParameterizedTest
    @CsvSource(
        "2025, 1, 1",
        "2025, 2, 0",
        "2025, 3, 0",
        "2025, 4, 2",
        "2025, 5, 2",
        "2025, 6, 0",
        "2025, 7, 0",
        "2025, 8, 1",
        "2025, 9, 0",
        "2025, 10, 0",
        "2025, 11, 0",
        "2025, 12, 2",
        "2027, 12, 3"
    )
    fun invoke(year: Int, month: Int, expect: Int) {
        assertEquals(expect, subject.invoke(year, month).size)
    }

}
