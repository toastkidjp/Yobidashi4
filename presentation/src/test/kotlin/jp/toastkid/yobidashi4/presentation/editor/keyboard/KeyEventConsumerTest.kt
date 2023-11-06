package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.web.search.SearchUrlFactory
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KeyEventConsumerTest {

    @InjectMockKs
    private lateinit var subject: KeyEventConsumer

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var searchUrlFactory: SearchUrlFactory

    private lateinit var awtKeyEvent: java.awt.event.KeyEvent

    @MockK
    private lateinit var multiParagraph: MultiParagraph

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        subject = KeyEventConsumer(mainViewModel, searchUrlFactory)
        every { searchUrlFactory.invoke(any()) } returns "https://search.yahoo.co.jp/search?p=test"
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun onKeyDown() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_PRESSED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_A,
            'A'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue(),
            mockk(),
            {}
        )

        assertFalse(consumed)
    }

    @Test
    fun duplicateSelectedText() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_RELEASED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_D,
            'D'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.", TextRange(6, 9)),
            mockk(),
            { assertEquals("Angel hashas fallen.", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun duplicateCurrentLine() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_RELEASED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_D,
            'D'
        )

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone."),
            multiParagraph,
            { assertEquals("Angel has fallen.\nAngel has fallen.\nHe has gone.", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopListConversionIfNotSelectedAnyText() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_RELEASED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_MINUS,
            '-'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone."),
            mockk(),
            {  }
        )

        assertFalse(consumed)
    }

    @Test
    fun listConversion() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_RELEASED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_MINUS,
            '-'
        )

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(0, 30)),
            multiParagraph,
            { assertEquals("- Angel has fallen.\n- He has gone.", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun noopOrderedListConversionIfNotSelectedAnyText() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_RELEASED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_1,
            '1'
        )

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone."),
            mockk(),
            {  }
        )

        assertFalse(consumed)
    }

    @Test
    fun orderedListConversion() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_RELEASED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_1,
            '1'
        )

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0
        every { multiParagraph.getLineEnd(0) } returns 17

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("Angel has fallen.\nHe has gone.", TextRange(0, 30)),
            multiParagraph,
            { assertEquals("1. Angel has fallen.\n2. He has gone.", it.text) }
        )

        assertTrue(consumed)
    }

    @Test
    fun combineLines() {
        awtKeyEvent = java.awt.event.KeyEvent(
            mockk(),
            java.awt.event.KeyEvent.KEY_RELEASED,
            1,
            java.awt.event.KeyEvent.CTRL_DOWN_MASK,
            java.awt.event.KeyEvent.VK_J,
            'J'
        )

        every { multiParagraph.getLineForOffset(any()) } returns 0
        every { multiParagraph.getLineStart(0) } returns 0

        val consumed = subject.invoke(
            KeyEvent(awtKeyEvent),
            TextFieldValue("a\nb\nc"),
            mockk(),
            { assertEquals("ab\nc", it.text) }
        )

        assertTrue(consumed)
    }

}