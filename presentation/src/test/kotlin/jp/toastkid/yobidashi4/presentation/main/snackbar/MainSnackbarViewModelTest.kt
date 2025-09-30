package jp.toastkid.yobidashi4.presentation.main.snackbar

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MainSnackbarViewModelTest {

    private lateinit var subject: MainSnackbarViewModel

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

        subject = MainSnackbarViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun setAnchor() {
        assertFalse(subject.isInitialized())

        subject.setAnchor(
            DraggableAnchors<Any> {
                Start at -20.dp.value
                Center at 0f
                End at 20.dp.value
            }
        )

        assertTrue(subject.isInitialized())

        assertNotNull(subject.anchoredDraggableState())
    }

    @Test
    fun offset() {
        assertEquals(0, subject.offset().x)
        assertEquals(0, subject.offset().y)
    }

    @Test
    fun isInProgress() {
        assertTrue(subject.overscrollEffect().isInProgress)
        CoroutineScope(Dispatchers.Unconfined).launch {
            subject.anchoredDraggableState().snapTo(Start)
        }
        assertFalse(subject.overscrollEffect().isInProgress)
    }

    @Test
    fun applyToFling() {
        CoroutineScope(Dispatchers.Unconfined).launch {
            subject.overscrollEffect().applyToFling(Velocity.Zero) { it }
            subject.anchoredDraggableState().snapTo(Start)
            subject.overscrollEffect().applyToFling(Velocity.Zero) { it }
        }

        verify { mainViewModel.dismissSnackbar() }
    }

    @Test
    fun applyToScroll() {
        subject.setAnchor(
            DraggableAnchors<Any> {
                Start at -20.dp.value
                Center at 0f
                End at 20.dp.value
            }
        )

        CoroutineScope(Dispatchers.Unconfined).launch {
            subject.overscrollEffect().applyToScroll(Offset.Zero, NestedScrollSource.UserInput) { it }
        }
    }

}