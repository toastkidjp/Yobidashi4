/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.article.finder

class KeywordSearchFilter(input: String?) {

    private val keywords: MutableSet<String> = mutableSetOf()

    private val useAndSearch: Boolean

    init {
        if (input == null) {
            useAndSearch = false
        } else if (input.contains("\"")) {
            keywords.add(input.replace("\"", ""))
            useAndSearch = false
        } else if (input.contains("*")) {
            input.split("*").forEach(keywords::add)
            useAndSearch = true
        } else if (input.contains(" ")) {
            input.split(" ").filter(String::isNotBlank).forEach(keywords::add)
            useAndSearch = false
        } else {
            keywords.add(input)
            useAndSearch = false
        }
    }

    operator fun invoke(text: String?): Boolean {
        if (text == null) {
            return false
        }

        if (keywords.isEmpty()) {
            return true
        }

        return if (useAndSearch) {
            keywords.all(text::contains)
        } else {
            keywords.any(text::contains)
        }
    }

}