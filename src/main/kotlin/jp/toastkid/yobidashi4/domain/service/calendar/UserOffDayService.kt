package jp.toastkid.yobidashi4.domain.service.calendar

import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserOffDayService: KoinComponent {

    private val setting: Setting by inject()

    private val userOffDays: List<Pair<Int, Int>> = setting.userOffDay()

    private val targetMonths = userOffDays.map { it.first }.distinct()

    operator fun invoke(month: Int, day: Int) =
        contains(month) && userOffDays.firstOrNull { it.first == month && it.second == day } != null

    private fun contains(month: Int): Boolean = targetMonths.contains(month)

}