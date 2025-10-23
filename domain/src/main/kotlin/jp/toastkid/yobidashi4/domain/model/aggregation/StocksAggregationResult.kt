/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.aggregation

class StocksAggregationResult : AggregationResult {

    private val dateAndSteps = mutableMapOf<String, Triple<Int, Int, Double>>()

    private val cache = mutableListOf<Array<Any>>()

    fun put(date: String, valuation: Int, gainOrLoss: Int, percent: Double) {
        dateAndSteps.put(date, Triple(valuation, gainOrLoss, percent))
    }

    override fun header(): Array<Any> {
        return arrayOf("Date", "Valuation", "Gain / Loss", "Percent")
    }

    override fun itemArrays(): Collection<Array<Any>> {
        if (cache.size != dateAndSteps.size) {
            cache.clear()
            dateAndSteps
                .map { arrayOf<Any>(it.key, it.value.first, it.value.second, it.value.third) }
                .forEach(cache::add)
        }

        return cache
    }

    override fun title(): String {
        return "資産運用成績"
    }

    override fun isEmpty(): Boolean {
        return dateAndSteps.isEmpty()
    }

    override fun columnClass(columnIndex: Int): Class<out Any> {
        return when (columnIndex) {
            1 -> Int::class.java
            2 -> Int::class.java
            3 -> Double::class.java
            else -> String::class.java
        }
    }

}