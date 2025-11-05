/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.component

import jp.toastkid.yobidashi4.domain.model.chat.GenerativeAiModel
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_amazon
import jp.toastkid.yobidashi4.library.resources.ic_filmarks
import jp.toastkid.yobidashi4.library.resources.ic_github
import jp.toastkid.yobidashi4.library.resources.ic_google_map
import jp.toastkid.yobidashi4.library.resources.ic_image
import jp.toastkid.yobidashi4.library.resources.ic_site_search
import jp.toastkid.yobidashi4.library.resources.ic_video
import jp.toastkid.yobidashi4.library.resources.ic_wikipedia
import jp.toastkid.yobidashi4.library.resources.ic_yahoo_japan_image_search
import jp.toastkid.yobidashi4.library.resources.ic_yahoo_japan_logo
import jp.toastkid.yobidashi4.library.resources.ic_yahoo_japan_realtime_search
import jp.toastkid.yobidashi4.presentation.chat.ChatModelIconMapper
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.jetbrains.compose.resources.DrawableResource

data class WebSearchItem(
    val label: String,
    val icon: DrawableResource,
    val useTint: Boolean,
    val action: (MainViewModel, String) -> Unit
) {

    companion object {

        fun from(generativeAiModel: GenerativeAiModel): WebSearchItem {
            return WebSearchItem(
                generativeAiModel.label(),
                ChatModelIconMapper().invoke(generativeAiModel),
                true,
            ) { viewModel, query ->
                viewModel.askGenerativeAi(query, generativeAiModel)
            }
        }

        fun fromSearchSite(searchSite: SearchSite): WebSearchItem {
            return WebSearchItem(
                searchSite.siteName,
                icon(searchSite),
                false,
                { viewModel, query ->
                    viewModel.openUrl(
                        searchSite.make(query, (viewModel.currentTab() as? WebTab)?.url()).toString(),
                        false
                    )
                }
            )
        }

        private fun icon(searchSite: SearchSite) = when(searchSite) {
            SearchSite.YAHOO_JAPAN -> Res.drawable.ic_yahoo_japan_logo
            SearchSite.WIKIPEDIA -> Res.drawable.ic_wikipedia
            SearchSite.GOOGLE_MAP -> Res.drawable.ic_google_map
            SearchSite.IMAGE_YAHOO_JAPAN -> Res.drawable.ic_yahoo_japan_image_search
            SearchSite.YOUTUBE -> Res.drawable.ic_video
            SearchSite.REALTIME_YAHOO_JAPAN -> Res.drawable.ic_yahoo_japan_realtime_search
            SearchSite.FILMARKS -> Res.drawable.ic_filmarks
            SearchSite.AMAZON -> Res.drawable.ic_amazon
            SearchSite.GITHUB -> Res.drawable.ic_github
            SearchSite.SEARCH_WITH_IMAGE -> Res.drawable.ic_image
            SearchSite.SITE_SEARCH -> Res.drawable.ic_site_search
        }

    }
}