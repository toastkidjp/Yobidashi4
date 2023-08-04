package jp.toastkid.yobidashi4.domain.service.article

import java.time.DayOfWeek

interface OffDayFinderService {
    operator fun invoke(year: Int, month: Int, date: Int, dayOfWeek: DayOfWeek, useUserOffDay: Boolean = true): Boolean
}