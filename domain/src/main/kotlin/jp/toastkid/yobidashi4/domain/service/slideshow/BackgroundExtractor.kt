/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.slideshow

import java.util.regex.Matcher
import java.util.regex.Pattern

class BackgroundExtractor {

    /**
     * Extract background image url from text.
     * @param line line
     * @return image url
     */
    operator fun invoke(line: String): String? {
        val matcher: Matcher = BACKGROUND.matcher(line)
        return if (matcher.find()) matcher.group(1) else null
    }

    companion object {

        /** Background image pattern.  */
        private val BACKGROUND: Pattern = Pattern.compile("\\!\\[background\\]\\((.+?)\\)")

        fun shouldInvoke(line: String) = line.startsWith("![background](")
    }

}