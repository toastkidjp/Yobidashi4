package jp.toastkid.yobidashi4.main

import jp.toastkid.yobidashi4.infrastructure.di.DependencyInjectionContainer
import jp.toastkid.yobidashi4.infrastructure.service.article.TodayArticleGeneratorImplementation
import jp.toastkid.yobidashi4.infrastructure.service.article.finder.AsynchronousArticleIndexerServiceImplementation
import jp.toastkid.yobidashi4.infrastructure.service.main.AppCloserThreadFactory
import jp.toastkid.yobidashi4.presentation.main.launchMainApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main() {
    DependencyInjectionContainer.start()

    CoroutineScope(Dispatchers.IO).launch {
        TodayArticleGeneratorImplementation().invoke()
        AsynchronousArticleIndexerServiceImplementation().invoke(Dispatchers.IO)
    }

    launchMainApplication()

    Runtime.getRuntime().addShutdownHook(AppCloserThreadFactory().invoke())
}
