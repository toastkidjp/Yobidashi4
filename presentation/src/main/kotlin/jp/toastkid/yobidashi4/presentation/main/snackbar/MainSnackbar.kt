package jp.toastkid.yobidashi4.presentation.main.snackbar

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MainSnackbar(snackbarData: SnackbarData) {
    val mainViewModel = remember { object : KoinComponent { val it: MainViewModel by inject() }.it }
    val dismissSnackbarDistance = with(LocalDensity.current) { 120.dp.toPx() }
    val anchors = DraggableAnchors {
        Start at -dismissSnackbarDistance.dp.value
        Center at 0f
        End at dismissSnackbarDistance.dp.value
    }

    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = Center,
            anchors = anchors,
        )
    }

    Snackbar(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary, modifier = Modifier
            .anchoredDraggable(
                state = anchoredDraggableState,
                orientation = Orientation.Horizontal,
                reverseDirection = false,
                flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                    anchoredDraggableState,
                    positionalThreshold = { it },
                    animationSpec = spring()
                ),
                overscrollEffect = object : OverscrollEffect {
                    override val isInProgress: Boolean
                        get() = anchoredDraggableState.currentValue == Center

                    override suspend fun applyToFling(
                        velocity: Velocity,
                        performFling: suspend (Velocity) -> Velocity
                    ) {
                        when (anchoredDraggableState.currentValue) {
                            Start, End -> mainViewModel.dismissSnackbar()
                            else -> anchoredDraggableState.snapTo(Center)
                        }
                    }

                    override fun applyToScroll(
                        delta: Offset,
                        source: NestedScrollSource,
                        performScroll: (Offset) -> Offset
                    ): Offset {
                        anchoredDraggableState.dispatchRawDelta(delta.x)
                        return delta
                    }

                }
            )
            .offset { IntOffset(anchoredDraggableState.requireOffset().toInt(), 0) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                snackbarData.message,
                modifier = Modifier.weight(1f)
            )

            val actionLabel = snackbarData.actionLabel
            if (actionLabel != null) {
                Text(
                    actionLabel,
                    modifier = Modifier
                        .clickable(onClick = snackbarData::performAction)
                        .wrapContentWidth()
                        .padding(start = 4.dp)
                )
            }
        }
    }
}