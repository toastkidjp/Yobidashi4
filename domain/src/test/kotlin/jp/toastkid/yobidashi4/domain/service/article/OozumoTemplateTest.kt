/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.article

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDate

class OozumoTemplateTest {

    private lateinit var subject: OozumoTemplate

    @BeforeEach
    fun setUp() {
        subject = OozumoTemplate()
    }

    @Test
    fun testGeneratedTitle() {
        assertTrue(subject.invoke(LocalDate.of(2024, 1, 14))?.startsWith("## 大相撲一月場所 初日") == true)
        assertTrue(subject.invoke(LocalDate.of(2024, 1, 22))?.startsWith("## 大相撲一月場所 九日目") == true)
    }

    @Test
    fun finalDateCase() {
        val finalDate = subject.invoke(LocalDate.of(2024, 1, 28))
        assertTrue(finalDate?.startsWith("## 大相撲一月場所 千秋楽") == true)
        assertTrue(finalDate?.contains("### 三賞") == true)
        assertTrue(finalDate?.contains("| 殊勲賞") == true)
        assertTrue(finalDate?.contains("| 敢闘賞") == true)
        assertTrue(finalDate?.contains("| 技能賞") == true)
        assertTrue(finalDate?.contains("### これより三役") == true)
    }

    @ParameterizedTest
    @CsvSource(
        "2024,1,9",
        "2024,1,29",
        "2024,2,15"
    )
    fun nullCases(year: Int, month: Int, date: Int) {
        assertNull(subject.invoke(LocalDate.of(year, month, date)))
    }

    @Test
    fun aroundDays() {
        (14..28).map { LocalDate.of(2024, 7, it) }.forEach {
            assertTrue(subject.invoke(it)?.contains("大相撲七月場所") == true)
        }
    }

    @Test
    fun aroundMonths() {
        val months = (1..12).mapNotNull { subject.invoke(LocalDate.of(2024, it, 20)) }
        assertEquals(6, months.size)
        months.forEach { assertTrue(it.startsWith("## 大相撲")) }
    }

}
