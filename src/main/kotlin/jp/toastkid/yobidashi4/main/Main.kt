package jp.toastkid.yobidashi4.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import java.io.IOException
import java.nio.file.Files
import javax.swing.JPopupMenu
import javax.swing.UIManager
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.browser.BrowserPool
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.article.ArticleTemplate
import jp.toastkid.yobidashi4.domain.service.article.ArticleTitleGenerator
import jp.toastkid.yobidashi4.infrastructure.di.DiModule
import jp.toastkid.yobidashi4.presentation.main.MultiTabContent
import jp.toastkid.yobidashi4.presentation.main.menu.MainMenu
import jp.toastkid.yobidashi4.presentation.main.snackbar.MainSnackbar
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
        makeTodayArticleIfNeed()
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

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = mainViewModel.snackbarHostState(),
                            snackbar = {
                                MainSnackbar(it) { mainViewModel.snackbarHostState().currentSnackbarData?.dismiss() }
                            }
                        )
                    }
                ) {
                    Box {
                        if (mainViewModel.backgroundImage().height != 0) {
                            Image(
                                mainViewModel.backgroundImage(),
                                "Background image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        MultiTabContent()
                    }
                }

                val dropTarget = DropTarget()
                dropTarget.addDropTargetListener(
                    object : DropTargetAdapter() {
                        override fun drop(dtde: DropTargetDropEvent?) {
                            dtde ?: return
                            try {
                                if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                                    dtde.acceptDrop(DnDConstants.ACTION_COPY)
                                    val transferable = dtde!!.transferable
                                    val list = transferable.getTransferData(
                                        DataFlavor.javaFileListFlavor
                                    ) as List<*>
                                    val files = list.filterIsInstance<File>().map { it.toPath() }.sortedBy { it.fileName.toString() }
                                    mainViewModel.emitDroppedPath(files)
                                    dtde!!.dropComplete(true)
                                    return
                                }
                            } catch (ex: UnsupportedFlavorException) {
                                LoggerFactory.getLogger(javaClass).warn("I/O error.", ex)
                            } catch (ex: IOException) {
                                LoggerFactory.getLogger(javaClass).warn("I/O error.", ex)
                            }
                            dtde!!.rejectDrop()
                        }
                    }
                )
                window.dropTarget = dropTarget
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

private fun makeTodayArticleIfNeed() {
    val title = ArticleTitleGenerator().invoke() ?: return
    val koin =  object : KoinComponent {
        val setting: Setting by inject()
        val articleFactory: ArticleFactory by inject()
    }
    val path = koin.setting.articleFolderPath().resolve("${title}.md")
    if (Files.exists(path)) {
        return
    }

    val article = koin.articleFactory.withTitle(title)
    article.makeFile { ArticleTemplate()(article.getTitle()) }
}
