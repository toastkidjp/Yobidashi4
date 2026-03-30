/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer

class FileNameInputTransformation : InputTransformation {

    private val invalidCharsRegex = Regex("""[\\/:*?"<>|]""")

    override fun TextFieldBuffer.transformInput() {
        if (!invalidCharsRegex.containsMatchIn(asCharSequence())) {
            return
        }

        val cleanText = asCharSequence().toString().replace(invalidCharsRegex, "")

        replace(0, length, cleanText)
    }

}