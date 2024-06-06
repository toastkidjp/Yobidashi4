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
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MainSnackbarKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                }
            )
        }

        every { mainViewModel.dismissSnackbar() } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

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
                                MainSnackbar(it)
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
                                MainSnackbar(it)
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
                                MainSnackbar(it)
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