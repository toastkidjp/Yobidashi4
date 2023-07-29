package jp.toastkid.yobidashi4.domain.service.calendar

interface UserOffDayService {
    operator fun invoke(month: Int, day: Int): Boolean
    fun contains(month: Int): Boolean
}