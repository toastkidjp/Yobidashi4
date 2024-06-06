package jp.toastkid.yobidashi4.presentation.main.snackbar

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
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
            positionalThreshold = { it },
            velocityThreshold = { 124.dp.value },
            animationSpec = spring(),
            confirmValueChange = {
                when (it) {
                    Start, End -> mainViewModel.dismissSnackbar()
                    else -> return@AnchoredDraggableState false
                }
                true
            }
        )
    }

    Snackbar(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary, modifier = Modifier
            .anchoredDraggable(
                state = anchoredDraggableState,
                orientation = Orientation.Horizontal
            )
            .offset { IntOffset(anchoredDraggableState.requireOffset().toInt(), 0) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                snackbarData.message,
                modifier = Modifier.weight(1f)
            )
            if (snackbarData.actionLabel != null) {
                Text(
                    snackbarData.actionLabel ?: "",
                    modifier = Modifier
                        .clickable(onClick = snackbarData::performAction)
                        .wrapContentWidth()
                        .padding(start = 4.dp)
                )
            }
        }
    }
}