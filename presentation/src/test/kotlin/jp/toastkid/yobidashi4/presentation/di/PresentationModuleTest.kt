package jp.toastkid.yobidashi4.presentation.di

import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.ksp.generated.module

class PresentationModuleTest {

    @Test
    fun checkKoinModules() {
        startKoin {
            modules(PresentationModule().module)
        }

        stopKoin()
    }

}