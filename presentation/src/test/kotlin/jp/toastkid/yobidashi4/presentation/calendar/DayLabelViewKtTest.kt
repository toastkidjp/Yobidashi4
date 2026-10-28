/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import org.junit.jupiter.api.Test
import java.time.DayOfWeek

class DayLabelViewKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun dayLabelView() {
        runDesktopComposeUiTest {
            setContent {
                DayLabelView(
                    1,
                    DayOfWeek.MONDAY,
                    listOf("test"),
                    true,
                    false,
                    Modifier
                )
                DayLabelView(
                    1,
                    DayOfWeek.MONDAY,
                    listOf("test"),
                    false,
                    true,
                    Modifier
                )
                DayLabelView(
                    1,
                    DayOfWeek.TUESDAY,
                    listOf("test"),
                    false,
                    false,
                    Modifier
                )
                DayLabelView(
                    1,
                    DayOfWeek.SATURDAY,
                    listOf("test"),
                    false,
                    false,
                    Modifier
                )
                DayLabelView(
                    1,
                    DayOfWeek.SUNDAY,
                    listOf("test"),
                    false,
                    false,
                    Modifier
                )
            }
        }
    }
}