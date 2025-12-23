/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.aggregation

data class MovieMemo(
    val date: String,
    val title: String
) {

    fun toArray(): Array<Any> = arrayOf(date, title)

    companion object {
        fun header(): Array<Any> {
            return arrayOf("Date", "Title")
        }
    }
}