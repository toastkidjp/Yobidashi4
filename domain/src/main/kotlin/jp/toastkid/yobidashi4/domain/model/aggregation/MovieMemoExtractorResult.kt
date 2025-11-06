/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.aggregation

class MovieMemoExtractorResult : AggregationResult {

    private val items = mutableListOf<MovieMemo>()

    private val cache: MutableList<Array<Any>> = mutableListOf()

    override fun header(): Array<Any> {
        return MovieMemo.header()
    }

    override fun itemArrays(): Collection<Array<Any>> {
        if (cache.size != items.size) {
            cache.clear()
            items
                .map(MovieMemo::toArray)
                .forEach(cache::add)
        }
        return cache
    }

    override fun title(): String {
        return String.format("Movies: %,d", items.size)
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    fun add(date: String, title: String) {
        items.add(MovieMemo(date, title))
    }

}