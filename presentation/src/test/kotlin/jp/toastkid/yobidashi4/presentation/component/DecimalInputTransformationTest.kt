/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.insert
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DecimalInputTransformationTest {

    private val transformation = DecimalInputTransformation()

    @Test
    fun testEmptyInput() {
        val state = TextFieldState("")

        // "3.4" を挿入
        state.edit {
            with(transformation) {
                transformInput()
            }
        }

        assertEquals("", state.text.toString())
    }

    @Test
    fun testAllowableInput() {
        val state = TextFieldState("12")

        // "3.4" を挿入
        state.edit {
            with(transformation) {
                insert(2, "3.4")
                transformInput()
            }
        }

        assertEquals("123.4", state.text.toString())
    }

    @Test
    fun testRevertIfContainsDot2Over() {
        val state = TextFieldState("1.2")

        // さらに "." を入力しようとする
        state.edit {
            with(transformation) {
                insert(3, ".")
                transformInput()
            }
        }

        // revertAllChanges() により、入力前の "1.2" に戻るはず
        assertEquals("1.2", state.text.toString())
    }

    @Test
    fun testRevertContainingAlphabet() {
        val state = TextFieldState("123")

        state.edit {
            with(transformation) {
                insert(3, "a")
                transformInput()
            }
        }

        assertEquals("123", state.text.toString())
    }

}