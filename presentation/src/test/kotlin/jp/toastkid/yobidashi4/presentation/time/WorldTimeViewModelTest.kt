package jp.toastkid.yobidashi4.presentation.time

import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.text.DateFormat
import java.time.ZoneId
import java.time.ZonedDateTime

class WorldTimeViewModelTest {

    private lateinit var subject: WorldTimeViewModel

    @BeforeEach
    fun setUp() {
        mockkStatic(DateFormat::class)

        subject = WorldTimeViewModel()
        subject.start()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun listState() {
        assertEquals(0, subject.listState().firstVisibleItemIndex)
    }

    @Test
    fun pickupTimeZone() {
        assertTrue(subject.pickupTimeZone().isNotEmpty())
    }

    @Test
    fun openingChooser() {
        assertFalse(subject.openingChooser())

        subject.openChooser()

        assertTrue(subject.openingChooser())

        subject.closeChooser()

        assertFalse(subject.openingChooser())
    }

    @Test
    fun choose() {
        subject.openChooser()

        subject.choose("Asia/Seoul")

        assertFalse(subject.openingChooser())
        println(subject.currentTimezoneLabel())
    }

    @Test
    fun openingHourChooser() {
        assertFalse(subject.openingHourChooser())

        subject.openHourChooser()

        assertTrue(subject.openingHourChooser())

        subject.closeHourChooser()

        assertFalse(subject.openingHourChooser())
    }

    @Test
    fun chooseHour() {
        subject.openHourChooser()

        subject.chooseHour(13)

        assertEquals("13", subject.currentHour())
        assertFalse(subject.openingHourChooser())
    }

    @Test
    fun openingMinuteChooser() {
        assertFalse(subject.openingMinuteChooser())

        subject.openMinuteChooser()

        assertTrue(subject.openingMinuteChooser())

        subject.closeMinuteChooser()

        assertFalse(subject.openingMinuteChooser())
    }

    @Test
    fun chooseMinute() {
        subject.openMinuteChooser()

        subject.chooseMinute(13)

        assertEquals("13", subject.currentMinute())
        assertFalse(subject.openingMinuteChooser())
    }

    @Test
    fun start() {
        assertTrue(subject.items().isNotEmpty())
    }

    @Test
    fun label() {
        assertEquals("\uD83C\uDDEF\uD83C\uDDF5 Tokyo", subject.label("Asia/Tokyo"))
    }

    @Test
    fun setDefault() {
        subject.setCurrentTime(ZonedDateTime.now(ZoneId.of("UTC")))
        subject.setDefault()

        assertEquals(ZonedDateTime.now().zone.id, subject.currentTimezoneLabel())
    }

}
