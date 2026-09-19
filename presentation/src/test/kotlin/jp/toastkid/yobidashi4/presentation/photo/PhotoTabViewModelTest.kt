package jp.toastkid.yobidashi4.presentation.photo

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.service.io.IoContextProvider
import jp.toastkid.yobidashi4.domain.service.photo.PhotoStreamLoader
import jp.toastkid.yobidashi4.domain.service.photo.gif.GifDivider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.io.IOException
import java.nio.file.Path
import javax.imageio.ImageIO

@OptIn(InternalComposeUiApi::class)
class PhotoTabViewModelTest {

    private lateinit var subject: PhotoTabViewModel

    @MockK
    private lateinit var gifDivider: GifDivider

    @MockK
    private lateinit var ioContextProvider: IoContextProvider
    
    @MockK
    private lateinit var photoStreamLoader: PhotoStreamLoader

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        val resourceAsStream = javaClass.classLoader.getResourceAsStream("icon/icon.png") ?: fail()
        every { photoStreamLoader(any()) } returns resourceAsStream

        coEvery { gifDivider.invoke(any()) } just Runs
        coEvery { ioContextProvider.invoke() } returns Dispatchers.Unconfined

        subject = PhotoTabViewModel(ioContextProvider, gifDivider, photoStreamLoader)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun bitmap() {
        subject.bitmap()
    }

    @Test
    fun switchMenu() {
        assertFalse(subject.visibleMenu())
        val openIcon = subject.handleIconPath()

        subject.switchMenu()

        assertTrue(subject.visibleMenu())
        assertNotEquals(subject.handleIconPath(), openIcon)
    }

    @Test
    fun alphaSliderPosition() {
        assertEquals(0f, subject.alphaSliderPosition())

        subject.setAlpha(1f)

        assertEquals(1f, subject.alphaSliderPosition())
    }

    @Test
    fun contrast() {
        assertEquals(0f, subject.contrast())

        subject.setContrast(1f)

        assertEquals(1f, subject.contrast())
    }

    @Test
    fun setSepia() {
        subject.setSepia()
        subject.setSepia()
    }

    @Test
    fun saturation() {
        subject.saturation()
    }

    @Test
    fun offset() {
        subject.offset()

        subject.setOffset(Offset.Zero)
    }

    @Test
    fun colorFilter() {
        subject.colorFilter()
    }

    @Test
    fun rotationZ() {
        assertEquals(0.0f, subject.rotationZ())
    }

    @Test
    fun onKeyEventWithScaleUp() {
        val consumed = subject.onKeyEvent(KeyEvent(Key.Semicolon, KeyEventType.KeyDown, isCtrlPressed = true))

        assertTrue(consumed)
    }

    @Test
    fun noopOnKeyEventWithCtrlMask() {
        val consumed = subject.onKeyEvent(KeyEvent(Key.Z, KeyEventType.KeyDown, isCtrlPressed = true))

        assertFalse(consumed)
    }

    @Test
    fun onKeyEventWithScaleDown() {
        val consumed = subject.onKeyEvent(KeyEvent(Key.Minus, KeyEventType.KeyDown, isCtrlPressed = true))

        assertTrue(consumed)
    }

    @Test
    fun onKeyEventOnSwitchMenu() {

        val consumed = subject.onKeyEvent(
            KeyEvent(Key.DirectionUp, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true)
        )

        assertTrue(consumed)
        assertTrue(subject.visibleMenu())

        val consumedOnDown = subject.onKeyEvent(
            KeyEvent(Key.DirectionDown, KeyEventType.KeyDown, isCtrlPressed = true, isShiftPressed = true)
        )

        assertTrue(consumedOnDown)
        assertFalse(subject.visibleMenu())
    }

    @Test
    fun onKeyEventWithRelease() {
        val consumed = subject.onKeyEvent(
            KeyEvent(Key.Semicolon, KeyEventType.KeyUp, isCtrlPressed = true)
        )

        assertFalse(consumed)
    }

    @Test
    fun onKeyEventElseCase() {
        val consumed = subject.onKeyEvent(KeyEvent(Key.Q, KeyEventType.KeyDown, isAltPressed = true))

        assertFalse(consumed)
    }

    @Test
    fun flipImage() {
        assertEquals(0f, subject.rotationY())

        subject.flipImage()

        assertEquals(180f, subject.rotationY())

        subject.flipImage()

        assertEquals(0f, subject.rotationY())
    }

    @Test
    fun reverseColor() {
        subject.reverseColor()
    }

    @Test
    fun launch() {
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } returns true
        val path = mockk<Path>()
        every { path.toFile() } returns mockk()

        subject.launch(path)

        verify { focusRequester.requestFocus() }
    }

    @Test
    fun launchWithIOException() {
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } returns true
        val path = mockk<Path>()
        every { path.toFile() } returns mockk()
        mockkStatic(ImageIO::class)
        every { ImageIO.read(any<File>()) } throws IOException("Expected")

        subject.launch(path)

        verify { focusRequester.requestFocus() }
        verify { photoStreamLoader(any()) }
    }

    @Test
    fun launchWithNullImage() {
        val snapshot = subject.bitmap()
        subject = spyk(subject)
        val focusRequester = mockk<FocusRequester>()
        every { subject.focusRequester() } returns focusRequester
        every { focusRequester.requestFocus() } returns true
        val path = mockk<Path>()
        every { path.toFile() } returns mockk()
        every { photoStreamLoader(any()) } returns "test".byteInputStream()

        subject.launch(path)

        verify { focusRequester.requestFocus() }
        verify { photoStreamLoader(any()) }
        assertSame(snapshot, subject.bitmap())
    }

    @Test
    fun resetStates() {
        subject.resetStates()

        assertEquals(1f, subject.scale())
    }

    @Test
    fun handleAlpha() {
        assertEquals(0f, subject.handleAlpha())

        subject.switchMenu()

        assertEquals(1f, subject.handleAlpha())

        subject.hideHandle()

        assertEquals(1f, subject.handleAlpha())

        subject.switchMenu()

        assertEquals(0f, subject.handleAlpha())

        subject.showHandle()

        assertEquals(1f, subject.handleAlpha())
    }

    @Test
    fun divideGif() {
        subject.divideGif(mockk())

        coVerify { gifDivider.invoke(any()) }
    }

    @Test
    fun focusRequester() {
        subject.focusRequester()
    }

    @Test
    fun state() = runTest {
        subject.state().transform { transformBy(1f) }
    }

}