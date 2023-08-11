package jp.toastkid.yobidashi4.domain.service.calendar
import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday

interface OffDayFinderService {

    operator fun invoke(
        year: Int,
        month: Int,
        useUserOffDay: Boolean = true
    ): List<Holiday>

}