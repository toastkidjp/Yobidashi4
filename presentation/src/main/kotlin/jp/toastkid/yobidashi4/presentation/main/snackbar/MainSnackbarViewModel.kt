package jp.toastkid.yobidashi4.presentation.main.snackbar

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainSnackbarViewModel : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val initialState = AnchoredDraggableState<Any>(
        initialValue = Center,
        anchors = DraggableAnchors { },
    )

    private val anchoredDraggableStateHolder =
        mutableStateOf<AnchoredDraggableState<Any>>(initialState)

    fun setAnchor(anchors: DraggableAnchors<Any>) {
        anchoredDraggableStateHolder.value = AnchoredDraggableState(
            initialValue = Center,
            anchors = anchors,
        )
    }

    fun anchoredDraggableState() = anchoredDraggableStateHolder.value

    fun overscrollEffect() = object : OverscrollEffect {
        override val isInProgress: Boolean
            get() = anchoredDraggableStateHolder.value.currentValue == Center

        override suspend fun applyToFling(
            velocity: Velocity,
            performFling: suspend (Velocity) -> Velocity
        ) {
            when (anchoredDraggableStateHolder.value.currentValue) {
                Start, End -> mainViewModel.dismissSnackbar()
                else -> anchoredDraggableStateHolder.value.snapTo(Center)
            }
        }

        override fun applyToScroll(
            delta: Offset,
            source: NestedScrollSource,
            performScroll: (Offset) -> Offset
        ): Offset {
            anchoredDraggableStateHolder.value.dispatchRawDelta(delta.x)
            return delta
        }

    }

    fun offset() = IntOffset(anchoredDraggableState().offset.toInt(), 0)

    fun isInitialized() = anchoredDraggableStateHolder.value !== initialState

}