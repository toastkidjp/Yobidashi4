package jp.toastkid.yobidashi4.domain.service.web

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import java.awt.Desktop
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UrlOpenerServiceTest {

    private lateinit var urlOpenerService: UrlOpenerService

    @MockK
    private lateinit var desktop: Desktop

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { desktop.browse(any()) }.just(Runs)

        urlOpenerService = UrlOpenerService(desktop)
    }

    @Test
    fun testNull() {
        urlOpenerService.invoke(null)

        verify(exactly = 0) { desktop.browse(any()) }
    }

    @Test
    fun testEmpty() {
        urlOpenerService.invoke("")

        verify(exactly = 0) { desktop.browse(any()) }
    }

    @Test
    fun testBlank() {
        urlOpenerService.invoke(" ")

        verify(exactly = 0) { desktop.browse(any()) }
    }

    @Test
    fun test() {
        urlOpenerService.invoke("https://www.yahoo.co.jp")

        verify(exactly = 1) { desktop.browse(any()) }
    }

}