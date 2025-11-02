/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.component

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WebSearchItemTest {

    @Test
    fun fromSearchSite() {
        val viewModel = mockk<MainViewModel>()
        every { viewModel.openUrl(any(), any()) } just Runs
        every { viewModel.askGenerativeAi(any(), any()) } just Runs
        every { viewModel.currentTab() } returns null

        val fromSearchSite = WebSearchItem.fromSearchSite(SearchSite.GITHUB)

        assertEquals(SearchSite.GITHUB.siteName, fromSearchSite.label)
        fromSearchSite.action(viewModel, "test")
        verify { viewModel.openUrl(any(), any()) }
        verify(inverse = true) { viewModel.askGenerativeAi(any(), any()) }
    }

}