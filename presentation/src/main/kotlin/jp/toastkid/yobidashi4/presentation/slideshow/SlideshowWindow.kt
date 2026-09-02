/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import jp.toastkid.yobidashi4.domain.service.slideshow.SlideDeckReader
import jp.toastkid.yobidashi4.presentation.main.theme.AppTheme
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path

class SlideshowWindow : KoinComponent {

    private val slideDeckReader: SlideDeckReader by inject()

    @Composable
    fun openWindow(
        path: Path,
        onCloseWindow: () -> Unit
    ) {
        val deck = slideDeckReader(path)
        if (deck.slides.isEmpty()) {
            return
        }

        AppTheme(darkTheme = false) {
            val viewModel = remember { SlideshowWindowViewModel() }
            Window(
                onCloseRequest = onCloseWindow,
                undecorated = true,
                state = viewModel.windowState(),
                visible = viewModel.windowVisible(),
                title = deck.title,
                onKeyEvent = viewModel::onKeyEvent
            ) {
                Slideshow(
                    deck,
                    viewModel::toggleFullscreen,
                    modifier = Modifier
                )
            }

            LaunchedEffect(path) {
                viewModel.setOnCloseWindow(onCloseWindow)
            }
        }
    }

}
