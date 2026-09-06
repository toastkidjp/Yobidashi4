package jp.toastkid.yobidashi4.infrastructure.di

import jp.toastkid.yobidashi4.presentation.di.PresentationModule
import okio.FileSystem
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.ksp.generated.module

class DependencyInjectionContainer {

    companion object {

        fun start() {
            startKoin {
                modules(InfrastructureModule().module)
                modules(PresentationModule().module)
                modules(
                    module {
                        single<FileSystem> { FileSystem.SYSTEM }
                    }
                )
            }
        }

        fun stop() {
            stopKoin()
        }

    }

}