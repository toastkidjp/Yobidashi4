package jp.toastkid.yobidashi4.presentation.editor.setting

import jp.toastkid.yobidashi4.presentation.editor.setting.ColorDecoderService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

internal class ColorDecoderServiceTest {

    private lateinit var colorDecoderService: ColorDecoderService

    @BeforeEach
    fun setUp() {
        colorDecoderService = ColorDecoderService()
    }

    @Test
    fun testReturnNull() {
        assertNull(colorDecoderService.invoke(null))
        assertNull(colorDecoderService.invoke(""))
        assertNull(colorDecoderService.invoke(" "))
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