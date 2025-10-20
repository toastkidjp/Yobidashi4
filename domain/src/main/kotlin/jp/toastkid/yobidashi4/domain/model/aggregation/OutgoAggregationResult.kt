/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.aggregation

class OutgoAggregationResult(val target: String): AggregationResult {

    private val map: MutableList<Outgo> = mutableListOf()

    private val cached: MutableList<Array<Any>> = mutableListOf()

    fun add(date: String, title: String, value: Int) {
        map.add(Outgo(date, title, value))
    }

    fun aggregate() {
        val aggregated = map
            .groupBy(Outgo::date)
            .map { Outgo(it.key, it.key, it.value.sumOf(Outgo::price)) }
        map.clear()
        map.addAll(aggregated)
    }

    fun sum(): Int {
        return map.sumOf(Outgo::price)
    }

    override fun itemArrays(): List<Array<Any>> {
        if (cached.size != map.size) {
            val mapped = map.map { arrayOf<Any>(it.date, it.title, it.price) }
            cached.clear()
            cached.addAll(mapped)
        }
        return cached
    }

    override fun header(): Array<Any> = arrayOf("Date", "Item", "Price")

    override fun title(): String {
        return String.format("Total: %,d", sum())
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    override fun columnClass(columnIndex: Int): Class<out Any> {
        return when (columnIndex) {
            2 -> Int::class.java
            else -> String::class.java
        }
    }

}