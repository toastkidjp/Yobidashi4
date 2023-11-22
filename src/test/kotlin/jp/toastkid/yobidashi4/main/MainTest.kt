package jp.toastkid.yobidashi4.main

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.infrastructure.di.DependencyInjectionContainer
import jp.toastkid.yobidashi4.infrastructure.service.article.TodayArticleGeneratorImplementation
import jp.toastkid.yobidashi4.presentation.main.launchMainApplication
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MainTest {

    @BeforeEach
    fun setUp() {
        mockkObject(DependencyInjectionContainer)
        every { DependencyInjectionContainer.start() } just Runs
        mockkConstructor(TodayArticleGeneratorImplementation::class)
        every { anyConstructed<TodayArticleGeneratorImplementation>().invoke() } just Runs
        mockkStatic("jp.toastkid.yobidashi4.presentation.main.MainApplicationKt")
        every { launchMainApplication() } just Runs
        mockkStatic(Runtime::class)
        every { Runtime.getRuntime().addShutdownHook(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test() {
        main()

        verify { DependencyInjectionContainer.start() }
        verify { launchMainApplication() }
        verify { Runtime.getRuntime().addShutdownHook(any()) }
    }

}