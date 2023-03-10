package jp.toastkid.yobidashi4.main

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import javax.swing.JPopupMenu
import javax.swing.UIManager
import jp.toastkid.yobidashi4.domain.model.browser.BrowserPool
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.article.TodayArticleGenerator
import jp.toastkid.yobidashi4.infrastructure.di.DiModule
import jp.toastkid.yobidashi4.presentation.main.MainScaffold
import jp.toastkid.yobidashi4.presentation.main.drop.DropTargetFactory
import jp.toastkid.yobidashi4.presentation.main.menu.MainMenu
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.cef.CefApp
import org.cef.CefApp.CefAppState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import org.slf4j.LoggerFactory

fun main() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    JPopupMenu.setDefaultLightWeightPopupEnabled(false)

    startKoin {
        modules(DiModule().module)
    }

    CoroutineScope(Dispatchers.IO).launch {
        TodayArticleGenerator().invoke()
    }

    application {
        val mainViewModel = remember { object : KoinComponent { val it: MainViewModel by inject() }.it }

        AppTheme(darkTheme = mainViewModel.darkMode()) {
            Window(
                onCloseRequest = {
                    exitApplication()
                },
                title = "Yobidashi 4",
                state = mainViewModel.windowState(),
                icon = painterResource("images/icon.png")
            ) {
                MainMenu { exitApplication() }

                MainScaffold()

                window.dropTarget = DropTargetFactory().invoke { mainViewModel.emitDroppedPath(it) }
            }
        }

        LaunchedEffect(Unit) {
            Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
                override fun uncaughtException(t: Thread?, e: Throwable?) {
                    LoggerFactory.getLogger(javaClass).error(t?.name, e)
                }
            })

            Runtime.getRuntime().addShutdownHook(Thread {
                if (CefApp.getState() == CefAppState.NONE) {
                    object : KoinComponent { val setting: Setting by inject() }.setting.save()
                    return@Thread
                }

                val koin = object : KoinComponent {
                    val setting: Setting by inject()
                    val browserPool: BrowserPool by inject()
                }
                koin.setting.save()
                koin.browserPool.disposeAll()
            })

            withContext(Dispatchers.IO) {
                mainViewModel.loadBackgroundImage()
            }
        }
    }
}
