package jp.toastkid.yobidashi4.presentation.main.snackbar

import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test

class MainSnackbarKtTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun mainSnackbar() {
        runDesktopComposeUiTest {
            setContent {
                val snackbarHostState = SnackbarHostState()

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            snackbar = {
                                MainSnackbar(it) { snackbarHostState.currentSnackbarData?.dismiss() }
                            }
                        )
                    }
                ) {

                }

                rememberCoroutineScope().launch {
                    snackbarHostState.showSnackbar("test")
                }
            }
        }
    }
}