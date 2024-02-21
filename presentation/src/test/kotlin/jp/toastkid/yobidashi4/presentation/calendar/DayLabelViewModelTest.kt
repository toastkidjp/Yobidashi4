package jp.toastkid.yobidashi4.presentation.calendar

import androidx.compose.ui.unit.sp
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.unmockkAll
import java.time.DayOfWeek
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DayLabelViewModelTest {

    @InjectMockKs
    private lateinit var subject: DayLabelViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun makeText() {
        assertTrue(subject.makeText(-1).isEmpty())
        assertEquals("1", subject.makeText(1))
        assertEquals("31", subject.makeText(31))
    }

    @Test
    fun textColor() {
        assertNotNull(subject.textColor(DayOfWeek.SUNDAY, true, true))
        assertNotNull(subject.textColor(DayOfWeek.SUNDAY, false, true))
        assertNotNull(subject.textColor(DayOfWeek.SUNDAY, false, false))
        assertNotNull(subject.textColor(DayOfWeek.SUNDAY, true, false))
        assertNotNull(subject.textColor(DayOfWeek.SATURDAY, true, true))
        assertNotNull(subject.textColor(DayOfWeek.SATURDAY, false, true))
        assertNotNull(subject.textColor(DayOfWeek.SATURDAY, false, false))
        assertNotNull(subject.textColor(DayOfWeek.SATURDAY, true, false))
        assertNotNull(subject.textColor(DayOfWeek.MONDAY, true, true))
        assertNotNull(subject.textColor(DayOfWeek.MONDAY, true, false))
        assertNotNull(subject.textColor(DayOfWeek.MONDAY, false, true))
        assertNull(subject.textColor(DayOfWeek.MONDAY, false, false))
    }

    @Test
    fun labelSize() {
        assertEquals(12.sp, subject.labelSize(null))
        assertEquals(12.sp, subject.labelSize(""))
        assertEquals(10.sp, subject.labelSize(" "))
        assertEquals(10.sp, subject.labelSize("test"))
    }

    @Test
    fun labelColor() {
        assertNotNull(subject.labelColor())
    }

    @Test
    fun useOffDayBackground() {
        assertTrue(subject.useOffDayBackground(true, DayOfWeek.SUNDAY))
        assertTrue(subject.useOffDayBackground(true, DayOfWeek.MONDAY))
        assertTrue(subject.useOffDayBackground(false, DayOfWeek.SATURDAY))
        assertTrue(subject.useOffDayBackground(false, DayOfWeek.SUNDAY))
        assertFalse(subject.useOffDayBackground(false, DayOfWeek.TUESDAY))
    }

}