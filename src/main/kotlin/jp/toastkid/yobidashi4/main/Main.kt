package jp.toastkid.yobidashi4.main

import jp.toastkid.yobidashi4.domain.model.browser.WebViewPool
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.infrastructure.di.DependencyInjectionContainer
import jp.toastkid.yobidashi4.infrastructure.service.article.TodayArticleGeneratorImplementation
import jp.toastkid.yobidashi4.presentation.main.launchMainApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

fun main() {
    DependencyInjectionContainer.start()

    CoroutineScope(Dispatchers.IO).launch {
        TodayArticleGeneratorImplementation().invoke()
    }

    launchMainApplication()

    Runtime.getRuntime().addShutdownHook(Thread {
        val koin = object : KoinComponent {
            val setting: Setting by inject()
            val webViewPool: WebViewPool by inject()
        }

        koin.setting.save()

        DependencyInjectionContainer.stop()
        koin.webViewPool.disposeAll()
    })
}
