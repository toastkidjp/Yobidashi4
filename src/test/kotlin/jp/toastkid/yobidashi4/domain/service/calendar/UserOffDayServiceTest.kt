package jp.toastkid.yobidashi4.domain.service.calendar

import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.setting.TestSettingImplementation
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * TODO Fix it
 */
internal class UserOffDayServiceTest {

    private lateinit var userOffDayService: UserOffDayService

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier=null) { TestSettingImplementation() } bind(Setting::class)
                }
            )
        }

        userOffDayService = UserOffDayService()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testInvoke() {
        assertFalse(userOffDayService.invoke(12, 3))
        assertFalse(userOffDayService.invoke(11, 29))
        assertTrue(userOffDayService.invoke(12, 29))
    }

}