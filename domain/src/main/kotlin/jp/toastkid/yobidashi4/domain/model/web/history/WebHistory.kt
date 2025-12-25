/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.web.history

data class WebHistory(
    val title: String,
    val url: String,
    val lastVisitedTime: Long = -1,
    val visitingCount: Int = 0
) {
    fun toTsv() = "$title$DELIMITER$url$DELIMITER$lastVisitedTime$DELIMITER$visitingCount"

}

const val DELIMITER = "\t"