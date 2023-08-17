package jp.toastkid.yobidashi4.infrastructure.service.barcode

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

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

}