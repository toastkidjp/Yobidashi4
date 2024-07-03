package jp.toastkid.yobidashi4.presentation.lib.clipboard

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.Image
import java.awt.datatransfer.Clipboard
import java.io.IOException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ClipboardPutterServiceTest {

    private lateinit var clipboardPutterService: ClipboardPutterService

    @MockK
    private lateinit var clipboard: Clipboard

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clipboardPutterService = ClipboardPutterService(clipboard)

        every { clipboard.setContents(any(), any()) }.answers { Unit }
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testInvoke() {
        clipboardPutterService("test")

        verify(exactly = 1) { clipboard.setContents(any(), any()) }
    }

    @Test
    fun testImage() {
        val image: Image = mockk()

        clipboardPutterService(image)

        verify(exactly = 1) { clipboard.setContents(any(), any()) }
    }

    @Test
    fun exceptionCase() {
        every { clipboard.setContents(any(), any()) } throws IOException()

        clipboardPutterService(mockk<Image>())

        verify(exactly = 1) { clipboard.setContents(any(), any()) }
    }

}