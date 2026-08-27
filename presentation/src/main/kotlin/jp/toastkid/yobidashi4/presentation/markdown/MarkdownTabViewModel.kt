/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.focus.FocusRequester
import jp.toastkid.yobidashi4.domain.model.tab.ScrollableContentTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MarkdownTabViewModel : KoinComponent {

    private val scrollState = LazyListState(0)

    private val focusRequester = FocusRequester()

    private val mainViewModel: MainViewModel by inject()

    suspend fun launch(scrollPosition: Int) {
        focusRequester().requestFocus()
        scrollState().scrollToItem(scrollPosition)
    }

    fun scrollState() = scrollState

    fun focusRequester() = focusRequester

    fun onDispose(tab: ScrollableContentTab) {
        mainViewModel.updateScrollableTab(tab, scrollState.firstVisibleItemIndex)
    }

}
