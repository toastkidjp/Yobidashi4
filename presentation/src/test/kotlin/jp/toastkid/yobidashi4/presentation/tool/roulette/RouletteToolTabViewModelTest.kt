package jp.toastkid.yobidashi4.presentation.tool.roulette

import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RouletteToolTabViewModelTest {

    private lateinit var subject: RouletteToolTabViewModel

    @BeforeEach
    fun setUp() {
        mockkConstructor(ClipboardPutterService::class)
        every { anyConstructed<ClipboardPutterService>().invoke(any<String>()) } just Runs

        subject = RouletteToolTabViewModel()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun onValueChange() {
        subject.onValueChange(TextFieldValue("test"))

        assertEquals("test", subject.input().text)

        subject.clearInput()

        assertTrue(subject.input().text.isEmpty())
    }

    @Test
    fun roulette() {
        assertTrue(subject.result().isEmpty())

        subject.roulette()

        assertTrue(subject.result().isNotBlank())

        subject.clipResult()

        verify { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

    @Test
    fun noopClipResult() {
        subject.clipResult()

        verify(inverse = true) { anyConstructed<ClipboardPutterService>().invoke(any<String>()) }
    }

}