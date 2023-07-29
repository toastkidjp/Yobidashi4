package jp.toastkid.yobidashi4.infrastructure.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.ksp.generated.module

@Module
@ComponentScan("jp.toastkid.yobidashi4.infrastructure")
class DependencyInjectionContainer {

    companion object {

        fun start() {
            startKoin {
                modules(DependencyInjectionContainer().module)
            }
        }

        fun stop() {
            stopKoin()
        }

    }

}