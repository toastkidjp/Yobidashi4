/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar.label

enum class FixedCalendarLabel(
    val month: Int, val date: Int, val title: String
) {
    APRIL_FOOLS_DAY(4, 1, "April fool's day"),
    MID_DAY(7, 2, "Mid day"),
    PREFECTURAL_CITIZENS_DAY(11, 13, "県民の日"),
    TOKYO_CITY_RACE(12, 29, "東京大賞典")
    ;
}