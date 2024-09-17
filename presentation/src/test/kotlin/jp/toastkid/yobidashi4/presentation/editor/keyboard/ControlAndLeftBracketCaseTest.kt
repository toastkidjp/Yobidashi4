package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ControlAndLeftBracketCaseTest {

    private lateinit var subject: ControlAndLeftBracketCase

    @MockK
    private lateinit var callback: (TextFieldValue) -> Unit

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { callback.invoke(any()) } just Runs

        subject = ControlAndLeftBracketCase()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun noop() {
        assertFalse(subject.invoke(TextFieldValue(), 1, callback))
        verify { callback wasNot Called }
    }

    @Test
    fun noopExtractedNormalCharacter() {
        assertFalse(subject.invoke(TextFieldValue("test"), 1, callback))
        verify { callback wasNot Called }
    }

    @Test
    fun noopExtractedNormal() {
        assertFalse(subject.invoke(TextFieldValue("(test"), 0, callback))
        verify { callback wasNot Called }
    }

    @ParameterizedTest
    @CsvSource(
        "(test), 0, ), 5, 6",
        "(test), 5, (, 0, 1",
        "[test], 0,], 5, 6",
        "[test], 5,[, 0, 1",
        "{test}, 0,}, 5, 6",
        "{test}, 5,{, 0, 1",
        "「test」, 0,」, 5, 6",
        "「test」, 5,「, 0, 1",
        "『test』, 0, 』, 5, 6",
        "『test』, 5,『, 0, 1"
    )
    fun correctCase(
        fieldValueText: String,
        selectionStartIndex: Int,
        expectedSelectedString: String,
        expectedSelectionStartIndex: Int,
        expectedSelectionEndIndex: Int
    ) {
        val slot = slot<TextFieldValue>()
        every { callback.invoke(capture(slot)) } just Runs

        val invoke = subject.invoke(TextFieldValue(fieldValueText), selectionStartIndex, callback)

        assertTrue(invoke)
        verify { callback.invoke(any()) }
        assertEquals(expectedSelectedString, slot.captured.getSelectedText().text)
        assertEquals(expectedSelectionStartIndex, slot.captured.selection.start)
        assertEquals(expectedSelectionEndIndex, slot.captured.selection.end)
    }

}