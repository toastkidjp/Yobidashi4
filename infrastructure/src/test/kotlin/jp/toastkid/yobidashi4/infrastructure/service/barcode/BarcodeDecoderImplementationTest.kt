package jp.toastkid.yobidashi4.infrastructure.service.barcode

import com.google.zxing.NotFoundException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import java.awt.image.BufferedImage

class BarcodeDecoderImplementationTest {

    private lateinit var barcodeDecoder: BarcodeDecoderImplementation

    @BeforeEach
    fun setUp() {
        barcodeDecoder = BarcodeDecoderImplementation()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun invoke() {
        val barcodeImage = BarcodeEncoderImplementation().invoke("https;//www.yahoo.co.jp", 100, 100)
            ?: fail("Failed generate barcode.")

        assertEquals("https;//www.yahoo.co.jp", barcodeDecoder.invoke(barcodeImage))
    }

    @Test
    fun testTransparentPixelEntersIfBlock() {
        val testImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)

        val transparentColor = 0x00FF0000
        testImage.setRGB(0, 0, transparentColor)

        assertThrows<NotFoundException> { barcodeDecoder.invoke(testImage) }
    }

}