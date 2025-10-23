/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.aggregation

class FindResult(private val keyword: String) : AggregationResult {

    private val items = mutableMapOf<String, List<String>>()

    private val cache: MutableList<Array<Any>> = mutableListOf()

    override fun header(): Array<Any> {
        return arrayOf("Name", "Lines")
    }

    override fun itemArrays(): Collection<Array<Any>> {
        if (cache.size != items.size) {
            cache.clear()
            items
                .map { arrayOf<Any>(it.key, it.value) }
                .forEach(cache::add)
        }
        return cache
    }

    override fun title(): String {
        return "'$keyword' find result ${items.size}"
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    fun add(fileName: String, filteredList: List<String>) {
        items.put(fileName, filteredList)
    }

    fun keyword() = keyword

    fun sortByTitle() {
        val sorted = items.toSortedMap()
        items.clear()
        items.putAll(sorted)
    }

}
