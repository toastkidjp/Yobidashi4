/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.aggregation

class StepsAggregationResult : AggregationResult {

    private val dateAndSteps = mutableMapOf<String, Pair<Int, Int>>()

    private val cache = mutableListOf<Array<Any>>()

    fun put(date: String, steps: Int, kcal: Int) {
        dateAndSteps.put(date, steps to kcal)
    }

    override fun header(): Array<Any> {
        return arrayOf("Date", "Steps", "Kcal")
    }

    override fun itemArrays(): Collection<Array<Any>> {
        if (cache.size != dateAndSteps.size) {
            cache.clear()
            dateAndSteps
                .map { arrayOf<Any>(it.key, it.value.first, it.value.second) }
                .forEach(cache::add)
        }

        return cache
    }

    override fun title(): String {
        return "歩数"
    }

    override fun isEmpty(): Boolean {
        return dateAndSteps.isEmpty()
    }

    override fun columnClass(columnIndex: Int): Class<out Any> {
        return when (columnIndex) {
            1 -> Int::class.java
            2 -> Int::class.java
            else -> String::class.java
        }
    }

}
