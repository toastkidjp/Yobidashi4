package jp.toastkid.yobidashi4.presentation.main.menu

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ContextMenuState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.TextContextMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLocalization
import jp.toastkid.yobidashi4.domain.service.text.TextCountMessageFactory
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel

class TextContextMenuFactory(private val mainViewModel: MainViewModel) {

    @OptIn(ExperimentalFoundationApi::class)
    operator fun invoke(): TextContextMenu {
        return object : TextContextMenu {
            @Composable
            override fun Area(
                textManager: TextContextMenu.TextManager,
                state: ContextMenuState,
                content: @Composable () -> Unit
            ) {
                val localization = LocalLocalization.current
                mainViewModel.setTextManager(textManager)
                val items = {
                    listOfNotNull(
                        textManager.cut?.let {
                            ContextMenuItem(localization.cut, it)
                        },
                        textManager.copy?.let {
                            ContextMenuItem(localization.copy, it)
                        },
                        textManager.paste?.let {
                            ContextMenuItem(localization.paste, it)
                        },
                        textManager.selectAll?.let {
                            ContextMenuItem(localization.selectAll, it)
                        },
                        ContextMenuItem("Search") {
                            mainViewModel
                                .openUrl("https://search.yahoo.co.jp/search?p=${textManager.selectedText.text}", false)
                        },
                        ContextMenuItem("Count") {
                            mainViewModel
                                .showSnackbar(TextCountMessageFactory().invoke(textManager.selectedText.text))
                        }
                    )
                }

                ContextMenuArea(items, state, content = content)
            }
        }
    }

}