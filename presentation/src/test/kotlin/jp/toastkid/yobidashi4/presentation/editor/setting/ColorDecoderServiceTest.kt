package jp.toastkid.yobidashi4.presentation.editor.setting

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ColorDecoderServiceTest {

    private lateinit var colorDecoderService: ColorDecoderService

    @BeforeEach
    fun setUp() {
        colorDecoderService = ColorDecoderService()
    }

    @ParameterizedTest
    @CsvSource(
        "null",
        "''",
        "' '",
        nullValues = ["null"]
    )
    fun testReturnNull(input: String?) {
        assertNull(colorDecoderService.invoke(input))
    }

    @Test
    fun test() {
        val color = colorDecoderService.invoke("#000099")
                ?: fail("This case doesn't allow null.")

        assertEquals(0, color.red)
        assertEquals(0, color.green)
        assertEquals(153, color.blue)
        assertEquals(255, color.alpha)
    }

    @Test
    fun testWithAlphaCode() {
        val color = colorDecoderService.invoke("#99990099")
                ?: fail("This case doesn't allow null.")

        assertEquals(153, color.red)
        assertEquals(0, color.green)
        assertEquals(153, color.blue)
        assertEquals(153, color.alpha)
    }

    @Test
    fun testWithoutSharp() {
        val color = colorDecoderService.invoke("99990099")
                ?: fail("This case doesn't allow null.")

        assertEquals(153, color.red)
        assertEquals(0, color.green)
        assertEquals(153, color.blue)
        assertEquals(153, color.alpha)
    }

}