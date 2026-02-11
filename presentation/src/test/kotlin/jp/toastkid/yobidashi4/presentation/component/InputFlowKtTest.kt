package jp.toastkid.yobidashi4.presentation.component/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
import androidx.compose.foundation.text.input.TextFieldState
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class InputFlowKtTest {

    @Test
    fun inputFlowOf() {
        val textFieldState = TextFieldState()
        val onInputChanged = mockk<() -> Unit>()
        every { onInputChanged.invoke() } just Runs

        runTest {
            CoroutineScope(Dispatchers.Unconfined)
                .launch {
                    collectCommittedInput(
                        textFieldState,
                        onInputChanged
                    )
                }
        }

        textFieldState.edit {
            append("test")
        }

        verify { onInputChanged.invoke() }
    }

}