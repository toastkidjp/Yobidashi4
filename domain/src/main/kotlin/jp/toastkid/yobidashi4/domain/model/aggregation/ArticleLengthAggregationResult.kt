/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.aggregation

class ArticleLengthAggregationResult : AggregationResult {

    private val map: MutableMap<String, Long> = hashMapOf()

    fun put(key: String, value: Long) {
        map[key] = value
    }

    private fun sum() = map.values.sum()

    override fun header(): Array<Any> =
            arrayOf("Title", "Length")

    override fun itemArrays(): Collection<Array<Any>> =
            map.entries.map { arrayOf(it.key, it.value) }

    override fun title(): String {
        return String.format("Total: %,d", sum())
    }

    override fun columnClass(columnIndex: Int): Class<out Any> {
        return when (columnIndex) {
            1 -> Int::class.java
            else -> String::class.java
        }
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

}