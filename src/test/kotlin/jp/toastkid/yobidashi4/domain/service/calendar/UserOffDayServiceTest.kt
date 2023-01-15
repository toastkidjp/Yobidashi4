package jp.toastkid.yobidashi4.domain.service.calendar

import io.mockk.every
import io.mockk.mockkConstructor
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * TODO Fix it
 */
internal class UserOffDayServiceTest {

    private lateinit var userOffDayService: UserOffDayService

    @BeforeEach
    fun setUp() {
        mockkConstructor(Setting::class)
        every { anyConstructed<Setting>().userOffDay() }.returns(listOf(12 to 29))
        userOffDayService = UserOffDayService()
    }

    @Test
    fun testInvoke() {
        assertFalse(userOffDayService.invoke(12, 3))
        assertFalse(userOffDayService.invoke(11, 29))
        assertTrue(userOffDayService.invoke(12, 29))
    }

}