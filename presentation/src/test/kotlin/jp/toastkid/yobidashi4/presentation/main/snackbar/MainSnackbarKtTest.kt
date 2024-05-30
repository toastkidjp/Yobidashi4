package jp.toastkid.yobidashi4.presentation.main.snackbar

import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
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
                    snackbarHostState.showSnackbar("test", "OK")
                    snackbarHostState.showSnackbar("test", "OK", SnackbarDuration.Long)
                }
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun swipeToEnd() {
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
                    snackbarHostState.showSnackbar("test-snackbar", "OK", SnackbarDuration.Indefinite)
                }
            }

            onNode(hasText("test-snackbar",)).performTouchInput {
                swipeRight(0f, 400f)
            }
        }
    }


    @OptIn(ExperimentalTestApi::class)
    @Test
    fun swipeLeft() {
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
                    snackbarHostState.showSnackbar("test-snackbar", "OK", SnackbarDuration.Indefinite)
                }
            }

            onNode(hasText("test-snackbar",)).performTouchInput {
                swipeLeft(0f, -400f)
            }
        }
    }

}