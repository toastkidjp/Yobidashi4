/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.service.converter.TwoStringConverterService
import jp.toastkid.yobidashi4.presentation.component.SingleLineTextField
import jp.toastkid.yobidashi4.presentation.component.collectCommittedInput
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun TwoValueConverterBox(unixTimeConverterService: TwoStringConverterService) {
    val viewModel = remember { TwoValueConverterBoxViewModel(unixTimeConverterService) }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Column {
            Text(unixTimeConverterService.title(), modifier = Modifier.padding(8.dp))

            SingleLineTextField(
                viewModel.firstInput(),
                unixTimeConverterService.firstInputLabel(),
                viewModel::clearFirstInput
            )

            LaunchedEffect(unixTimeConverterService) {
                collectCommittedInput(viewModel.firstInput()) {
                    viewModel.onFirstValueChange()
                }
            }

            SingleLineTextField(
                viewModel.secondInput(),
                unixTimeConverterService.secondInputLabel(),
                viewModel::clearSecondInput
            )

            LaunchedEffect(unixTimeConverterService) {
                snapshotFlow { viewModel.secondInput().text to (viewModel.firstInput().composition != null) }
                    .distinctUntilChanged()
                    .collect {
                        if (it.second) {
                            return@collect
                        }
                        viewModel.onSecondValueChange()
                    }
            }
        }
    }
}