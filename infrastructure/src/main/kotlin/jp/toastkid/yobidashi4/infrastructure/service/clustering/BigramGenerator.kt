/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.clustering

class BigramGenerator {

    private val stopWords = setOf("食べ", "を食", "室温")

    private val regex = Regex("[\\p{IsHan}\\p{IsHira}\\p{IsKana}]{2}")

    private fun isKanjiCharacter(char: Char): Boolean = char in '\u4E00'..'\u9FFF'

    operator fun invoke(text: Pair<String, String>): List<String> = text.second
        .replace("\n", "")
        .windowed(2, 1)
        .filter(CharSequence::isNotBlank)
        .filter { matchRegex(it) }
        .filter { it.any(::isKanjiCharacter) }
        .filter { stopWords.none { stopWord -> it.contains(stopWord) } }

    private fun matchRegex(string: String): Boolean = string.matches(regex)

}