package jp.toastkid.yobidashi4.domain.service.converter

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.unmockkAll
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UrlEncodeConverterServiceTest {

    @InjectMockKs
    private lateinit var urlEncodeConverterService: UrlEncodeConverterService

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
        assertTrue(urlEncodeConverterService.title().isNotBlank())
    }

    @Test
    fun defaultFirstInputValue() {
        assertTrue(urlEncodeConverterService.defaultFirstInputValue().isNotBlank())
    }

    @Test
    fun defaultSecondInputValue() {
        assertTrue(urlEncodeConverterService.defaultSecondInputValue().isNotBlank())
    }

    @Test
    fun firstInputAction() {
        assertEquals("", urlEncodeConverterService.firstInputAction(""))
        assertEquals("+", urlEncodeConverterService.firstInputAction(" "))
        assertEquals("%E6%9D%B1%E4%BA%AC%E7%89%B9%E8%A8%B1+%E8%A8%B1%E5%8F%AF%E5%B1%80", urlEncodeConverterService.firstInputAction("東京特許 許可局"))
    }

    @Test
    fun secondInputAction() {
        assertEquals("", urlEncodeConverterService.secondInputAction(""))
        assertEquals(" ", urlEncodeConverterService.secondInputAction(" "))
        assertEquals("東京特許 許可局", urlEncodeConverterService.secondInputAction("%E6%9D%B1%E4%BA%AC%E7%89%B9%E8%A8%B1+%E8%A8%B1%E5%8F%AF%E5%B1%80"))
    }
}