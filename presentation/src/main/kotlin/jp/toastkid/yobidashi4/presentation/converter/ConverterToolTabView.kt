/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.service.converter.DistanceConverterService
import jp.toastkid.yobidashi4.domain.service.converter.JapaneseAgeConverterService
import jp.toastkid.yobidashi4.domain.service.converter.JapaneseEraConverterService
import jp.toastkid.yobidashi4.domain.service.converter.TatamiCountConverterService
import jp.toastkid.yobidashi4.domain.service.converter.TemperatureConverterService
import jp.toastkid.yobidashi4.domain.service.converter.TsuboCountConverterService
import jp.toastkid.yobidashi4.domain.service.converter.UnixTimeConverterService
import jp.toastkid.yobidashi4.domain.service.converter.UrlEncodeConverterService

@Composable
fun ConverterToolTabView() {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember { ConverterToolTabViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = Modifier

    ) {
        Box {
            Column(modifier = Modifier
                .padding(8.dp)
                .verticalScroll(viewModel.scrollState())
                .fillMaxWidth()
                .onKeyEvent { viewModel.keyboardScrollAction(coroutineScope, it.key, it.isCtrlPressed) }
                .focusRequester(viewModel.focusRequester())
                .semantics { contentDescription = "surface" }
            ) {
                TwoValueConverterBox(UnixTimeConverterService())
                TwoValueConverterBox(UrlEncodeConverterService())
                TwoValueConverterBox(TatamiCountConverterService())
                TwoValueConverterBox(TsuboCountConverterService())
                TwoValueConverterBox(TemperatureConverterService())
                TwoValueConverterBox(DistanceConverterService())
                TwoValueConverterBox(JapaneseEraConverterService())
                TwoValueConverterBox(JapaneseAgeConverterService())
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.scrollState()),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )

            LaunchedEffect(Unit) {
                viewModel.launch()
            }
        }
    }
}