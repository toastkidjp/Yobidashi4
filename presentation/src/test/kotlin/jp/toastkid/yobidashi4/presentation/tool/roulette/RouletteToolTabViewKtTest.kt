/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.tool.roulette

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RouletteToolTabViewKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(RouletteToolTabViewModel::class)

        every { anyConstructed<RouletteToolTabViewModel>().input() } returns TextFieldState()
        every { anyConstructed<RouletteToolTabViewModel>().result() } returns "test"
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        runDesktopComposeUiTest {
            setContent {
                RouletteToolTabView()
            }
        }
    }


    @OptIn(ExperimentalTestApi::class)
    @Test
    fun resultIsEmptyCase() {
        runDesktopComposeUiTest {
            setContent {
                every { anyConstructed<RouletteToolTabViewModel>().result() } returns ""

                RouletteToolTabView()
            }
        }
    }

}