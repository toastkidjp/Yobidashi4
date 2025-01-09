package jp.toastkid.yobidashi4.infrastructure.service.main

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class AppCloserThreadFactoryTest {

    private lateinit var subject: AppCloserThreadFactory

    @MockK
    private lateinit var setting: Setting

    @MockK
    private lateinit var webViewPool: WebViewPool


    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { setting } bind(Setting::class)
                    single(qualifier = null) { webViewPool } bind(WebViewPool::class)
                }
            )
        }
        mockkConstructor(AppCloserAction::class)
        every { anyConstructed<AppCloserAction>().invoke() } just Runs

        subject = AppCloserThreadFactory()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun invoke() {
        val thread = subject.invoke()
        thread.start()
        while (thread.state != Thread.State.TERMINATED) {
            Thread.sleep(100L)
        }

        verify { anyConstructed<AppCloserAction>().invoke() }
    }

}