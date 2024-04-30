package jp.toastkid.yobidashi4.main

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.infrastructure.di.DependencyInjectionContainer
import jp.toastkid.yobidashi4.infrastructure.service.article.TodayArticleGeneratorImplementation
import jp.toastkid.yobidashi4.infrastructure.service.article.finder.AsynchronousArticleIndexerServiceImplementation
import jp.toastkid.yobidashi4.infrastructure.service.main.AppCloserThreadFactory
import jp.toastkid.yobidashi4.presentation.main.launchMainApplication
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MainTest {

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { setting.articleFolderPath() } returns mockk()
        startKoin {
            modules(
                module {
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        mockkObject(DependencyInjectionContainer)
        every { DependencyInjectionContainer.start() } just Runs
        mockkConstructor(
            TodayArticleGeneratorImplementation::class,
            AppCloserThreadFactory::class,
            AsynchronousArticleIndexerServiceImplementation::class
        )
        every { anyConstructed<TodayArticleGeneratorImplementation>().invoke() } just Runs
        every { anyConstructed<AppCloserThreadFactory>().invoke() } returns mockk()
        every { anyConstructed<AsynchronousArticleIndexerServiceImplementation>().invoke() } just Runs
        mockkStatic("jp.toastkid.yobidashi4.presentation.main.MainApplicationKt")
        every { launchMainApplication() } just Runs
        mockkStatic(Runtime::class)
        every { Runtime.getRuntime().addShutdownHook(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun test() {
        main()

        verify { DependencyInjectionContainer.start() }
        verify { launchMainApplication() }
        verify { Runtime.getRuntime().addShutdownHook(any()) }
        verify { anyConstructed<AppCloserThreadFactory>().invoke() }
        verify { anyConstructed<AsynchronousArticleIndexerServiceImplementation>().invoke() }
    }

}