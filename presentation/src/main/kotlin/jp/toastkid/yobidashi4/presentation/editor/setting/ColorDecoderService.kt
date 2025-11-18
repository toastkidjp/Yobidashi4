/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.setting

import java.awt.Color

class ColorDecoderService {

    operator fun invoke(argbColorCode: String?): Color? {
        if (argbColorCode.isNullOrBlank()) {
            return null
        }

        val code = if (argbColorCode.startsWith("#")) argbColorCode else "#$argbColorCode"
        return Color(java.lang.Long.decode(code).toInt(), code.length > 7)
    }

}