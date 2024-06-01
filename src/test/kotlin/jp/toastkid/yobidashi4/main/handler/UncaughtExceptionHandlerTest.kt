package jp.toastkid.yobidashi4.main.handler

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UncaughtExceptionHandlerTest {

    private lateinit var subject: UncaughtExceptionHandler

    @MockK
    private lateinit var logger: Logger

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(LoggerFactory::class)
        every { LoggerFactory.getLogger(any<Class<Any>>()) } returns logger
        every { logger.error(any<String>(), any()) } just Runs

        subject = UncaughtExceptionHandler()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun uncaughtException() {
        val throwable = mockk<Throwable>()

        subject.uncaughtException(Thread.currentThread(), throwable)

        verify { logger.error(any(), throwable) }
    }

    @Test
    fun nullCase() {
        val throwable = mockk<Throwable>()

        subject.uncaughtException(null, throwable)

        verify { logger.error(null, throwable) }
    }

}