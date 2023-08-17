package jp.toastkid.yobidashi4.domain.service.converter

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UnixTimeConverterServiceTest {

    @InjectMockKs
    private lateinit var unixTimeConverterService: UnixTimeConverterService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun title() {
        assertTrue(unixTimeConverterService.title().isNotBlank())
    }

    @Test
    fun defaultFirstInputValue() {
        assertTrue(unixTimeConverterService.defaultFirstInputValue().isNotBlank())
    }

    @Test
    fun defaultSecondInputValue() {
        assertTrue(unixTimeConverterService.defaultSecondInputValue().isNotBlank())
    }

    @Test
    fun firstInputAction() {
        assertNull(unixTimeConverterService.firstInputAction(""))
        assertNull(unixTimeConverterService.firstInputAction(" "))
        assertNull(unixTimeConverterService.firstInputAction("test1"))
        assertEquals("1970-01-01 09:00:02", unixTimeConverterService.firstInputAction("2000"))

        assertEquals("1970-01-01 08:59:59", unixTimeConverterService.firstInputAction("-1"))
        assertEquals("1970-01-01 09:00:00", unixTimeConverterService.firstInputAction("0"))
        assertEquals("1970-01-01 09:00:00", unixTimeConverterService.firstInputAction("1"))
        assertEquals("1970-01-01 09:00:00", unixTimeConverterService.firstInputAction("100"))
        assertEquals("1975-01-27 08:50:33", unixTimeConverterService.firstInputAction("160012233432"))
        assertEquals("2020-09-15 07:25:34", unixTimeConverterService.firstInputAction("1600122334320"))
        assertEquals("2477-01-21 17:15:43", unixTimeConverterService.firstInputAction("16001223343200"))
        assertEquals("7040-08-02 19:37:12", unixTimeConverterService.firstInputAction("160012233432000"))
    }

    @Test
    fun secondInputAction() {
        assertNull(unixTimeConverterService.secondInputAction(""))
        assertNull(unixTimeConverterService.secondInputAction(" "))
        assertNull(unixTimeConverterService.secondInputAction("test2"))
        assertNull(unixTimeConverterService.secondInputAction("2023-02-13"))
        assertNull(unixTimeConverterService.secondInputAction("-9-02-13 11:22:33"))
        assertNull(unixTimeConverterService.secondInputAction("9-02-13 11:22:33"))
        assertNull(unixTimeConverterService.secondInputAction("99-02-13 11:22:33"))
        assertNull(unixTimeConverterService.secondInputAction("999-02-13 11:22:33"))
        assertNull(unixTimeConverterService.secondInputAction("-009-02-13 11:22:33"))
        assertEquals("-61879412247000", unixTimeConverterService.secondInputAction("0009-02-13 11:22:33"))
        assertEquals("-59039271447000", unixTimeConverterService.secondInputAction("0099-02-13 11:22:33"))
        assertEquals("-30638036247000", unixTimeConverterService.secondInputAction("0999-02-13 11:22:33"))
        assertEquals("-217201047000", unixTimeConverterService.secondInputAction("1963-02-13 11:22:33"))
        assertEquals("1676254953000", unixTimeConverterService.secondInputAction("2023-02-13 11:22:33"))
        assertEquals("2370565353000", unixTimeConverterService.secondInputAction("2045-02-13 11:22:33"))
    }

    @Test
    fun checkValues() {
        assertTrue(unixTimeConverterService.title().isNotBlank())
        assertTrue(unixTimeConverterService.firstInputLabel().isNotBlank())
        assertTrue(unixTimeConverterService.secondInputLabel().isNotBlank())
        assertTrue(unixTimeConverterService.defaultFirstInputValue().isNotBlank())
        assertTrue(unixTimeConverterService.defaultSecondInputValue().isNotBlank())
    }
    
}