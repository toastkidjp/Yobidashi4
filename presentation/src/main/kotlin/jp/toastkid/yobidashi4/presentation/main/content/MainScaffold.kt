/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import jp.toastkid.yobidashi4.presentation.lib.annotation.ExcludeCoverageCalculation
import jp.toastkid.yobidashi4.presentation.main.snackbar.MainSnackbar
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@ExcludeCoverageCalculation
@Composable
fun MainScaffold() {
    MainScaffold(koinInject())
}

@Composable
fun MainScaffold(mainViewModel: MainViewModel) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = mainViewModel.snackbarHostState(),
                snackbar = {
                    MainSnackbar(it)
                }
            )
        }
    ) {
        Box {
            if (mainViewModel.showBackgroundImage()) {
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

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        mainViewModel.snackbarEventFlow().collect { event ->
            coroutineScope.launch {
                val snackbarHostState = mainViewModel.snackbarHostState()
                snackbarHostState.currentSnackbarData?.dismiss()
                val snackbarResult = snackbarHostState.showSnackbar(
                    event.message,
                    event.actionLabel,
                    event.duration
                )
                when (snackbarResult) {
                    SnackbarResult.Dismissed -> Unit
                    SnackbarResult.ActionPerformed -> {
                        event.action()
                    }
                }
            }
        }
    }
}