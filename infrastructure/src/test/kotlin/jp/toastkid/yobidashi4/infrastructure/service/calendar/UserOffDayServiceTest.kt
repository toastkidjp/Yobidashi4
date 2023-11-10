package jp.toastkid.yobidashi4.infrastructure.service.calendar

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.setting.Setting
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

    private lateinit var userOffDayService: UserOffDayServiceImplementation

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { setting.userOffDay() } returns listOf(12 to 29, 12 to 30, 12 to 31)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        userOffDayService = UserOffDayServiceImplementation()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun testInvoke() {
        assertFalse(userOffDayService.invoke(12, 3))
        assertFalse(userOffDayService.invoke(11, 29))
        assertTrue(userOffDayService.invoke(12, 29))
    }

    @Test
    fun findBy() {
        assertTrue(userOffDayService.findBy(6).isEmpty())
    }

}