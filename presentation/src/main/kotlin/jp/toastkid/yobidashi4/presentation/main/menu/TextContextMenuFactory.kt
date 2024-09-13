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

                val itemConsumer = {
                    val items = mutableListOf<ContextMenuItem>()
                    val cut = textManager.cut
                    if (cut != null) {
                        items.add(ContextMenuItem(localization.cut, cut))
                    }
                    val copy = textManager.copy
                    if (copy != null) {
                        items.add(ContextMenuItem(localization.copy, copy))
                    }
                    val paste = textManager.paste
                    if (paste != null) {
                        items.add(ContextMenuItem(localization.paste, paste))
                    }
                    val selectAll = textManager.selectAll
                    if (selectAll != null) {
                        items.add(ContextMenuItem(localization.selectAll, selectAll))
                    }
                    items.add(
                        ContextMenuItem("Search") {
                            mainViewModel.webSearch(textManager.selectedText.text)
                        }
                    )
                    items.add(
                        ContextMenuItem("Count") {
                            mainViewModel
                                .showSnackbar(TextCountMessageFactory().invoke(textManager.selectedText.text))
                        }
                    )
                    items.add(
                        ContextMenuItem("Find article") {
                            mainViewModel
                                .findArticle(TextCountMessageFactory().invoke(textManager.selectedText.text))
                        }
                    )

                    items
                }

                ContextMenuArea(itemConsumer, state, content = content)
            }
        }
    }

}