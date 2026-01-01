package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.text.TextRange
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.service.editor.LinkDecoratorService
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardFetcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class LinkPasteCaseTest {

    private lateinit var subject: LinkPasteCase

    @MockK
    private lateinit var selectedTextConversion: SelectedTextConversion

    @MockK
    private lateinit var linkDecoratorService: LinkDecoratorService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { selectedTextConversion.invoke(any(), any(), any(), any()) } returns true
        startKoin {
            modules(
                module {
                    single(qualifier=null) { linkDecoratorService } bind(LinkDecoratorService::class)
                }
            )
        }

        subject = LinkPasteCase()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun toDecoratedLink() {
        val selected = "https://test.yahoo.com"
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns selected
        val decoratedLink = "[test]($selected)"
        every { linkDecoratorService.invoke(any()) } returns decoratedLink
        val capturingSlot = slot<(String) -> String?>()
        every { selectedTextConversion.invoke(any(), any(), any(), capture(capturingSlot)) } returns true
        val content = TextFieldState(selected, TextRange(0))

        val consumed = subject.invoke(
            content,
            0,
            selected.length,
            selectedTextConversion
        )

        assertTrue(consumed)
        verify { selectedTextConversion.invoke(any(), any(), any(), any()) }
        assertTrue(capturingSlot.isCaptured)
        assertEquals("[test](https://test.yahoo.com)", capturingSlot.captured.invoke("test"))
        verify(inverse = true) { anyConstructed<ClipboardFetcher>().invoke()  }
    }

    @ParameterizedTest
    @CsvSource(
        "https://test.yahoo.com",
        "http://test.yahoo.com"
    )
    fun pasteDecoratedLink(url: String) {
        mockkConstructor(ClipboardFetcher::class, LinkDecoratorService::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns url
        val decoratedLink = "[test]($url)"
        every { linkDecoratorService.invoke(any()) } returns decoratedLink
        val content = TextFieldState("", TextRange(0))

        val consumed = subject.invoke(
            content,
            0,
            0,
            selectedTextConversion
        )

        assertTrue(consumed)
        verify { selectedTextConversion wasNot Called }
        assertEquals(decoratedLink, content.text)
    }

    @Test
    fun noopClipboardCase() {
        val selected = "https://test.yahoo.com"
        mockkConstructor(ClipboardFetcher::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns null

        val consumed = subject.invoke(
            TextFieldState(selected),
            0,
            0,
            selectedTextConversion,
        )

        assertFalse(consumed)
        verify { selectedTextConversion wasNot called }
    }

    @Test
    fun noopPasteBecauseClippingNotUrlCase() {
        mockkConstructor(ClipboardFetcher::class, LinkDecoratorService::class)
        every { anyConstructed<ClipboardFetcher>().invoke() } returns "not URL"
        val decoratedLink = "[test](https://test.yahoo.com)"
        every { linkDecoratorService.invoke(any()) } returns decoratedLink

        val consumed = subject.invoke(
            TextFieldState("", TextRange(0)),
            0,
            0,
            selectedTextConversion
        )

        assertTrue(consumed)
        verify { selectedTextConversion wasNot Called }
    }

}