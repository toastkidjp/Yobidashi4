package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import jp.toastkid.yobidashi4.presentation.lib.annotation.ExcludeCoverageCalculation
import jp.toastkid.yobidashi4.presentation.main.snackbar.MainSnackbar
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
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
}