package jp.toastkid.yobidashi4.domain.service.converter

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.time.LocalDate
import java.time.chrono.JapaneseDate
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JapaneseAgeConverterServiceTest {

    private lateinit var japaneseAgeConverterService: JapaneseAgeConverterService

    @BeforeEach
    fun setUp() {
        japaneseAgeConverterService = JapaneseAgeConverterService()

        mockkStatic(JapaneseDate::class)
        every { JapaneseDate.now() }.returns(JapaneseDate.of(2023, 8, 27))
        mockkStatic(LocalDate::class)
        every { LocalDate.now() }.returns(LocalDate.of(2023, 8, 27))
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun title() {
        assertNotNull(japaneseAgeConverterService.title())
    }

    @Test
    fun firstInputLabel() {
        assertNotNull(japaneseAgeConverterService.firstInputLabel())
    }

    @Test
    fun secondInputLabel() {
        assertNotNull(japaneseAgeConverterService.secondInputLabel())
    }

    @Test
    fun defaultFirstInputValue() {
        assertNotNull(japaneseAgeConverterService.defaultFirstInputValue())
    }

    @Test
    fun defaultSecondInputValue() {
        assertNotNull(japaneseAgeConverterService.defaultSecondInputValue())
    }

    @Test
    fun firstInputAction() {
        assertNull(japaneseAgeConverterService.firstInputAction("明治5"))
        assertEquals("35", japaneseAgeConverterService.firstInputAction("昭和63"))
        assertEquals("33", japaneseAgeConverterService.firstInputAction("平成2"))
        assertEquals("4", japaneseAgeConverterService.firstInputAction("令和1"))
        assertEquals("113", japaneseAgeConverterService.firstInputAction("明治43"))
        assertEquals("99", japaneseAgeConverterService.firstInputAction("大正13"))
    }

    @Test
    fun firstInputActionIrregularCase() {
        assertNull(japaneseAgeConverterService.firstInputAction("大正"))
        assertNull(japaneseAgeConverterService.firstInputAction("1984"))
        assertNull(japaneseAgeConverterService.firstInputAction("大正浪漫"))
    }

    @Test
    fun secondInputAction() {
        assertNull(japaneseAgeConverterService.secondInputAction("2019"))
        assertNull(japaneseAgeConverterService.secondInputAction("大正"))
        assertEquals("大正14", japaneseAgeConverterService.secondInputAction("98"))
        assertEquals("大正15", japaneseAgeConverterService.secondInputAction("97"))
        assertEquals("昭和2", japaneseAgeConverterService.secondInputAction("96"))
        assertEquals("昭和62", japaneseAgeConverterService.secondInputAction("36"))
        assertEquals("昭和63", japaneseAgeConverterService.secondInputAction("35"))
        assertEquals("平成1", japaneseAgeConverterService.secondInputAction("34"))
        assertEquals("令和1", japaneseAgeConverterService.secondInputAction("4"))
        assertEquals("令和2",  japaneseAgeConverterService.secondInputAction("3"))
    }
}