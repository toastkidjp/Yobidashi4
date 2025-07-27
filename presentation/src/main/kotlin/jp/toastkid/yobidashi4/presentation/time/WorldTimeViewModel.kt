package jp.toastkid.yobidashi4.presentation.time

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.DecimalFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.zone.ZoneRulesException
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.atomic.AtomicReference

class WorldTimeViewModel : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val currentTime = AtomicReference(ZonedDateTime.now())

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd(E)HH:mm:ss", Locale.ENGLISH)

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

    fun pickupTimeZone() = pickupTimeZone

    fun start() {
        updateItems()
    }

    fun updateItems() {
        items.clear()

        val currentTime = currentTime.get()
        availableIDs.mapNotNull {
            try {
                val zoneId = ZoneId.of(it)
                WorldTime(zoneId, formatter.format(currentTime.withZoneSameInstant(zoneId)))
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
            else -> timeZoneId
        }
    }

    fun onClickItem(it: WorldTime) {
        val text = label(it.timeZone()) + " " + it.time
        ClipboardPutterService().invoke(text)

        mainViewModel.showSnackbar("Copy to clipboard.: $text")
    }

    private val openChooser = mutableStateOf(false)

    fun openingChooser() = openChooser.value

    fun openChooser() {
        openChooser.value = true
    }

    fun closeChooser() {
        openChooser.value = false
    }

    fun choose(value: String) {
        val calendar = currentTime.get()
        setCurrentTime(calendar.withZoneSameInstant(ZoneId.of(value)))
        closeChooser()
    }

    private val openHourChooser = mutableStateOf(false)

    fun openingHourChooser() = openHourChooser.value

    fun openHourChooser() {
        openHourChooser.value = true
    }

    fun closeHourChooser() {
        openHourChooser.value = false
    }

    fun chooseHour(value: Int) {
        val calendar = currentTime.get()
        setCurrentTime(calendar.withHour(value))
        closeHourChooser()
    }

    private val openMinutesChooser = mutableStateOf(false)

    fun openingMinuteChooser() = openMinutesChooser.value

    fun openMinuteChooser() {
        openMinutesChooser.value = true
    }

    fun closeMinuteChooser() {
        openMinutesChooser.value = false
    }

    fun chooseMinute(value: Int) {
        val calendar = currentTime.get()
        setCurrentTime(calendar.withMinute(value))
        closeMinuteChooser()
    }

    private val zeroPaddingFormatter = DecimalFormat("00")

    private val currentHour = mutableStateOf("")

    fun currentHour(): String = currentHour.value

    private val currentMinute = mutableStateOf("")

    fun currentMinute(): String = currentMinute.value

    fun currentTimezoneLabel(): String {
        return currentTime.get().zone.id
    }

    fun setCurrentTime(newTime: ZonedDateTime) {
        currentTime.set(newTime)
        currentHour.value = zeroPaddingFormatter.format(currentTime.get().hour)
        currentMinute.value = zeroPaddingFormatter.format(currentTime.get().minute)
        updateItems()
    }

}