package jp.toastkid.yobidashi4.presentation.time

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.zone.ZoneRulesException
import java.util.TimeZone

class WorldTimeViewModel {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd(E)HH:mm:ss")

    private val listState = LazyListState()

    private val items = mutableStateListOf<WorldTime>()

    fun listState() = listState

    fun items(): List<WorldTime> = items

    private val pickupTimeZone = listOf(
        "Asia/Tokyo",
        "UTC",
        "America/New_York",
        "US/Pacific",
        "US/Hawaii",
        "Europe/Rome",
        "Australia/Sydney",
        "NZ",
        "Asia/Ho_Chi_Minh",
        "IST"
    )

    private val availableIDs = pickupTimeZone.plus(TimeZone.getAvailableIDs())

    fun start() {
        items.clear()

        availableIDs.mapNotNull {
            try {
                val zoneId = ZoneId.of(it)
                WorldTime(zoneId, formatter.format(LocalDateTime.now(zoneId)))
            } catch (e: ZoneRulesException) {
                null
            }
        }
            .forEach { items.add(it) }
    }

    fun label(timeZoneId: String): String {
        return when (timeZoneId) {
            "Asia/Tokyo" ->"\uD83C\uDDEF\uD83C\uDDF5 Tokyo"
            "UTC" -> "UTC"
            "America/New_York" -> "\uD83C\uDDFA\uD83C\uDDF8 New York"
            "US/Pacific" -> "\uD83C\uDDFA\uD83C\uDDF8 US Pacific"
            "US/Hawaii" -> "Hawaii"
            "Europe/Rome" -> "\uD83C\uDDEE\uD83C\uDDF9 Rome"
            "Australia/Sydney" -> "\uD83C\uDDE6\uD83C\uDDFA Sydney"
            "NZ" -> "\uD83C\uDDF3\uD83C\uDDFF New Zealand"
            "Asia/Ho_Chi_Minh" -> "\uD83C\uDDFB\uD83C\uDDF3 Ho Chi Minh"
            "IST" -> "\uD83C\uDDEE\uD83C\uDDF3 IST"
            else -> timeZoneId
        }
    }

}