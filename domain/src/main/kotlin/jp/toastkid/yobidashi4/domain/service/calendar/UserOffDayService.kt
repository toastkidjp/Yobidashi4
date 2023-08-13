package jp.toastkid.yobidashi4.domain.service.calendar

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday

interface UserOffDayService {
    operator fun invoke(month: Int, day: Int): Boolean
    fun contains(month: Int): Boolean

    fun findBy(month: Int): Set<Holiday>
}