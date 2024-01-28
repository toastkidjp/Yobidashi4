package jp.toastkid.yobidashi4.domain.service.article

import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OozumoTemplateTest {

    private lateinit var subject: OozumoTemplate

    @BeforeEach
    fun setUp() {
        subject = OozumoTemplate()
    }

    @Test
    fun test() {
        assertNull(subject.invoke(LocalDate.of(2024, 1, 9)))
        assertTrue(subject.invoke(LocalDate.of(2024, 1, 14))?.startsWith("## 大相撲一月場所 初日") ?: false)
        assertTrue(subject.invoke(LocalDate.of(2024, 1, 22))?.startsWith("## 大相撲一月場所 九日目") ?: false)
        val finalDate = subject.invoke(LocalDate.of(2024, 1, 28))
        assertTrue(finalDate?.startsWith("## 大相撲一月場所 千秋楽") ?: false)
        assertTrue(finalDate?.contains("### これより三役") ?: false)
        assertNull(subject.invoke(LocalDate.of(2024, 1, 29)))
        assertNull(subject.invoke(LocalDate.of(2024, 2, 15)))
    }

    @Test
    fun aroundDays() {
        (14..28).map { LocalDate.of(2024, 7, it) }.forEach {
            assertTrue(subject.invoke(it)?.contains("大相撲七月場所") ?: false)
        }
    }

    @Test
    fun aroundMonths() {
        val months = (1..12).map { subject.invoke(LocalDate.of(2024, it, 20)) }.filterNotNull()
        assertEquals(6, months.size)
        months.forEach { assertTrue(it.startsWith("## 大相撲")) }
    }

}