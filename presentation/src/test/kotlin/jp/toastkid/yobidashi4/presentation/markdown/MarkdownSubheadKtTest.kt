/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.markdown.Subhead
import jp.toastkid.yobidashi4.presentation.markdown.MarkdownSubhead
import org.junit.jupiter.api.Test

class MarkdownSubheadKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun markdownSubhead() {
        val function: (Int) -> Unit = mockk()
        every { function.invoke(any()) } just Runs

        runDesktopComposeUiTest {
            setContent {
                MarkdownSubhead(
                    listOf(Subhead("1st", 1, 0)),
                    function
                )
            }

            onNode(hasText("1st")).performClick()
            verify { function.invoke(any()) }
        }
    }

}