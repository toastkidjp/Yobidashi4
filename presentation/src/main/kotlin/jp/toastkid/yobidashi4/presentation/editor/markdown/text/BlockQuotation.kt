/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.markdown.text

class BlockQuotation {

    /**
     * Invoke quotation function.
     *
     * @param text Nullable [CharSequence]
     */
    operator fun invoke(text: String?): String? {
        if (text.isNullOrEmpty()) {
            return text
        }
        val converted = text.trimEnd().split(LINE_SEPARATOR)
                .asSequence()
                .map { "> $it" }
                .reduce { str1, str2 -> str1 + LINE_SEPARATOR + str2 }
        return if (text.endsWith(LINE_SEPARATOR)) converted.plus(LINE_SEPARATOR) else converted
    }
}

/**
 * Line separator.
 */
private const val LINE_SEPARATOR = "\n"