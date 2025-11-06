/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar

import java.time.DayOfWeek

data class CalendarDate(
    val date: Int,
    val dayOfWeek: DayOfWeek,
    val label: List<String> = emptyList(),
    val offDay: Boolean = false
)