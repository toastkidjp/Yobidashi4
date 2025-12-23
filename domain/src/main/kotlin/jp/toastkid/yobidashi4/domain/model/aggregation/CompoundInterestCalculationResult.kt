/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.aggregation

class CompoundInterestCalculationResult : AggregationResult {

    private val items = mutableListOf<Triple<Int, Long, Long>>()

    private val cache: MutableList<Array<Any>> = mutableListOf()

    override fun header(): Array<Any> {
        return arrayOf("Year", "Single", "Compound")
    }

    override fun itemArrays(): Collection<Array<Any>> {
        if (cache.size != items.size) {
            cache.clear()
            items
                .map { arrayOf<Any>(it.first, it.second, it.third) }
                .forEach(cache::add)
        }
        return cache
    }

    override fun columnClass(columnIndex: Int): Class<out Any> {
        if (columnIndex == 1 || columnIndex == 2) {
            return Long::class.java
        }
        return Int::class.java
    }

    override fun title(): String {
        return " compound interests"
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    fun get(year: Int): Triple<Int, Long, Long>? {
        return items.firstOrNull { it.first == year }
    }

    fun put(year: Int, singleCase: Long, compoundCase: Long) {
        items.add(Triple(year, singleCase, compoundCase))
    }

}
