package jp.toastkid.yobidashi4.infrastructure.service.calendar

import jp.toastkid.yobidashi4.domain.model.calendar.holiday.Holiday
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.calendar.UserOffDayService
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class UserOffDayServiceImplementation: KoinComponent, UserOffDayService {

    private val setting: Setting by inject()

    private val userOffDays: List<Pair<Int, Int>> = setting.userOffDay()

    private val targetMonths = userOffDays.map { it.first }.distinct()

    override operator fun invoke(month: Int, day: Int) =
        contains(month) && userOffDays.firstOrNull { it.first == month && it.second == day } != null

    override fun contains(month: Int): Boolean = targetMonths.contains(month)

    override fun findBy(month: Int): Set<Holiday> {
        return userOffDays.filter { it.first == month }.map { Holiday("User off-day", it.first, it.second) }.toSet()
    }

}