/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck
import jp.toastkid.yobidashi4.presentation.slideshow.view.SlideView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Slideshow(
    deck: SlideDeck,
    onEscapeKeyReleased: () -> Unit,
    onFullscreenKeyReleased: () -> Unit,
    modifier: Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { deck.slides.size }
    val viewModel = remember { SlideshowViewModel() }

    LaunchedEffect(deck) {
        withContext(Dispatchers.IO) {
            viewModel.launch(deck, onEscapeKeyReleased, onFullscreenKeyReleased)
        }
    }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = modifier
            .onKeyEvent {
                viewModel.onKeyEvent(coroutineScope, it, pagerState)
            }
            .focusRequester(viewModel.focusRequester())
    ) {
        Box(
            modifier = Modifier.padding(8.dp).fillMaxHeight()
        ) {
            val backgroundUrl = deck.background
            if (backgroundUrl.isNotBlank()) {
                val bitmap = viewModel.loadImage(backgroundUrl)
                Image(
                    bitmap,
                    "Background image",
                    modifier = Modifier.fillMaxSize()
                )
            }

            HorizontalPager(
                pageSize = PageSize.Fill,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) {
                val slide = deck.slides.get(it)

                SlideView(slide, viewModel::loadImage)

                LaunchedEffect(Unit) {
                    viewModel.requestFocus()
                }
            }

            if (deck.footerText.isNotBlank()) {
                Text(
                    deck.footerText,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }

            Text(
                "${pagerState.currentPage + 1} / ${deck.slides.size}",
                modifier = Modifier.align(Alignment.BottomEnd)
            )

            val alpha = animateFloatAsState(viewModel.sliderAlpha())
            Slider(
                viewModel.sliderValue(),
                onValueChange = viewModel::setSliderValue,
                onValueChangeFinished = {
                    val page = viewModel.sliderValue().roundToInt()
                    if (page != pagerState.currentPage) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page)
                        }
                    }
                },
                valueRange = 0f .. (deck.slides.size - 1).toFloat(),
                steps = max(1, deck.slides.size - 2),
                modifier = Modifier.align(Alignment.BottomCenter)
                    .alpha(alpha.value)
                    .onPointerEvent(PointerEventType.Enter) {
                        viewModel.showSlider()
                    }
                    .onPointerEvent(PointerEventType.Exit) {
                        viewModel.hideSlider()
                    }
                    .semantics { contentDescription = "slider" }
            )

            LaunchedEffect(pagerState.currentPage) {
                viewModel.setSliderValue(pagerState.currentPage.toFloat())
            }
        }
    }
}
