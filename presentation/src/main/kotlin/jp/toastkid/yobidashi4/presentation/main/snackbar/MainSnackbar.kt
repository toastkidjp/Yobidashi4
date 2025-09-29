/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.snackbar

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MainSnackbar(snackbarData: SnackbarData) {
    val viewModel = remember { MainSnackbarViewModel() }

    val dismissSnackbarDistance = with(LocalDensity.current) { 120.dp.toPx() }

    LaunchedEffect(dismissSnackbarDistance) {
        viewModel.setAnchor(
            DraggableAnchors {
                Start at -dismissSnackbarDistance.dp.value
                Center at 0f
                End at dismissSnackbarDistance.dp.value
            }
        )
    }

    if (!viewModel.isInitialized()) {
        return
    }

    Snackbar(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary, modifier = Modifier
            .anchoredDraggable(
                state = viewModel.anchoredDraggableState(),
                orientation = Orientation.Horizontal,
                reverseDirection = false,
                flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                    viewModel.anchoredDraggableState(),
                    positionalThreshold = { it },
                    animationSpec = spring()
                ),
                overscrollEffect = viewModel.overscrollEffect()
            )
            .offset { viewModel.offset() }
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
