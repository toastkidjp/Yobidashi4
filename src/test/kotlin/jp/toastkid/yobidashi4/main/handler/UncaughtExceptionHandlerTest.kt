package jp.toastkid.yobidashi4.main.handler

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.slf4j.Logger

class UncaughtExceptionHandlerTest {

    private lateinit var subject: UncaughtExceptionHandler

    @MockK
    private lateinit var logger: Logger

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { mockk<Setting>() } bind(Setting::class)
                    single(qualifier=null) { mockk<WebViewPool>() } bind(WebViewPool::class)
                }
            )
        }

        subject = UncaughtExceptionHandler()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun uncaughtException() {
        val throwable = mockk<Throwable>()

        subject.uncaughtException(Thread.currentThread(), throwable)

        //TODO Fix verify { logger.error(any(), throwable) }
    }

    @Test
    fun nullCase() {
        val throwable = mockk<Throwable>()

        subject.uncaughtException(null, throwable)

        //TODO Fix verify { logger.error(null, throwable) }
    }

}