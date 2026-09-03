/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.toOffset
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.slideshow.Slide
import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TextLine
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SlideshowKtTest {

    private lateinit var slideDeck: SlideDeck

    private val scrollEventFlow = MutableSharedFlow<Int>(extraBufferCapacity = 1)

    @BeforeEach
    fun setUp() {
        mockkConstructor(SlideshowViewModel::class)
        every { anyConstructed<SlideshowViewModel>().focusRequester() } returns FocusRequester()
        every { anyConstructed<SlideshowViewModel>().requestFocus() } just Runs
        every { anyConstructed<SlideshowViewModel>().scrollEventFlow() } returns scrollEventFlow
        every { anyConstructed<SlideshowViewModel>().loadImage(any()) } returns ImageBitmap(1, 1)

        slideDeck = SlideDeck()
        slideDeck.add(Slide().also { it.addLine(TextLine("test")) })
        slideDeck.add(Slide().also { it.setBackground("test.jpg") })
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun slideshow() {
        runComposeUiTest {
            setContent {
                Slideshow(
                    slideDeck,
                    {},
                    Modifier
                )
            }

            onNodeWithContentDescription("slider", useUnmergedTree = true)
                .assertExists("Not found!")
                .performMouseInput {
                    enter()
                    click()
                    press()
                    moveBy(IntOffset(-20, 0).toOffset())
                    release()
                    exit()
                }
                .performKeyInput {
                    pressKey(Key.K, 1000L)
                    pressKey(Key.DirectionRight, 1000L)
                    pressKey(Key.DirectionRight, 1000L)
                    pressKey(Key.DirectionRight, 1000L)
                    pressKey(Key.DirectionLeft, 1000L)
                }
            verify { anyConstructed<SlideshowViewModel>().requestFocus() }

            verify { anyConstructed<SlideshowViewModel>().scrollEventFlow() }
            scrollEventFlow.tryEmit(1)

            verify { anyConstructed<SlideshowViewModel>().loadImage(any()) }
        }
    }

}