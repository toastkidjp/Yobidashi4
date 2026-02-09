/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.calendar

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday

interface UserOffDayService {
    operator fun invoke(month: Int, day: Int): Boolean
    fun contains(month: Int): Boolean

    fun findBy(month: Int): Set<Holiday>
}