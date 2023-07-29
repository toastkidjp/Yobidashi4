package jp.toastkid.yobidashi4.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import jp.toastkid.yobidashi4.presentation.main.snackbar.MainSnackbar
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun MainScaffold() {
    val mainViewModel = remember { object : KoinComponent { val it: MainViewModel by inject() }.it }

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
}