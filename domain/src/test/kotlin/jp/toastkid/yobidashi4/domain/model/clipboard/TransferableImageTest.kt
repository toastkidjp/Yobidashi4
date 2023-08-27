package jp.toastkid.yobidashi4.domain.model.clipboard

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import java.awt.Image
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TransferableImageTest {

    @InjectMockKs
    private lateinit var transferableImage: TransferableImage

    @MockK
    private lateinit var image: Image

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun getTransferData() {
        val transferData = transferableImage.getTransferData(DataFlavor.imageFlavor)

        assertSame(image, transferData)
    }

    @Test
    fun getTransferData_invalidFlavor() {
        assertThrows<UnsupportedFlavorException> { transferableImage.getTransferData(DataFlavor.stringFlavor) }
    }

    @Test
    fun getTransferDataFlavors() {
        val transferDataFlavors = transferableImage.transferDataFlavors

        assertEquals(1, transferDataFlavors.size)
        assertEquals(DataFlavor.imageFlavor, transferDataFlavors[0])
    }

    @Test
    fun isDataFlavorSupported() {
        assertTrue(transferableImage.isDataFlavorSupported(DataFlavor.imageFlavor))
        assertFalse(transferableImage.isDataFlavorSupported(DataFlavor.stringFlavor))
        assertFalse(transferableImage.isDataFlavorSupported(DataFlavor.allHtmlFlavor))
        assertFalse(transferableImage.isDataFlavorSupported(DataFlavor.selectionHtmlFlavor))
        assertFalse(transferableImage.isDataFlavorSupported(DataFlavor.fragmentHtmlFlavor))
        assertFalse(transferableImage.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
    }
}