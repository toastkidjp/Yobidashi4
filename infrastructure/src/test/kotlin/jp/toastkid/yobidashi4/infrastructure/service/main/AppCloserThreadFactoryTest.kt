package jp.toastkid.yobidashi4.infrastructure.service.main

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.infrastructure.di.DependencyInjectionContainer
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

        every { setting.save() } just Runs
        every { webViewPool.disposeAll() } just Runs
        mockkObject(DependencyInjectionContainer)
        every { DependencyInjectionContainer.stop() } just Runs

        subject = AppCloserThreadFactory()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun invoke() {
        subject.invoke().start()

        verify { setting.save() }
        verify { webViewPool.disposeAll() }
        verify { DependencyInjectionContainer.stop() }
    }

}