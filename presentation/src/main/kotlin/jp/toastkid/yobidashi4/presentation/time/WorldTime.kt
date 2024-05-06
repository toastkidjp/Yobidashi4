package jp.toastkid.yobidashi4.presentation.time

import java.time.ZoneId

data class WorldTime(
    val timeZoneId: ZoneId,
    val time: String
) {

    fun timeZone(): String = timeZoneId.id

}