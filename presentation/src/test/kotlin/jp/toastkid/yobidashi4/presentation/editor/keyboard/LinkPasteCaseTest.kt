package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.LinkDecoratorService
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardFetcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LinkPasteCaseTest {

    private lateinit var subject: LinkPasteCase

    @MockK
    private lateinit var selectedTextConversion: SelectedTextConversion

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { selectedTextConversion.invoke(any(), any(), any(), any(), any()) } returns true

        subject = LinkPasteCase()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun pasteDecoratedLink() {
        mockkConstructor(ClipboardFetcher::class, LinkDecoratorService::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "https://test.yahoo.com"
        val decoratedLink = "[test](https://test.yahoo.com)"
        every { anyConstructed<LinkDecoratorService>().invoke(any()) } returns decoratedLink

        val consumed = subject.invoke(
            TextFieldValue("", TextRange(0)),
            0,
            0,
            selectedTextConversion,
            { assertEquals(decoratedLink, it.text) }
        )

        assertTrue(consumed)
        verify { selectedTextConversion wasNot Called }
    }

    @Test
    fun toDecoratedLink() {
        val selected = "https://test.yahoo.com"
        mockkConstructor(ClipboardFetcher::class, LinkDecoratorService::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns selected
        val decoratedLink = "[test]($selected)"
        every { anyConstructed<LinkDecoratorService>().invoke(any()) } returns decoratedLink

        val consumed = subject.invoke(
            TextFieldValue(selected, TextRange(0)),
            0,
            selected.length,
            selectedTextConversion,
            { assertEquals(decoratedLink, it.text) }
        )

        assertTrue(consumed)
        verify { selectedTextConversion.invoke(any(), any(), any(), any(), any()) }
    }

    @Test
    fun noopClipboardCase() {
        val selected = "https://test.yahoo.com"
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns null

        val consumed = subject.invoke(
            TextFieldValue(selected),
            0,
            0,
            selectedTextConversion,
            { fail() }
        )

        assertFalse(consumed)
        verify { selectedTextConversion wasNot called }
    }

}