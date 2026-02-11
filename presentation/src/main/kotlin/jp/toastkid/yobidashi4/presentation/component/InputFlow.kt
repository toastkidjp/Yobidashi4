/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/**
 * Observe TextFieldState's input, and call passed action on commit input.
 */
suspend fun collectCommittedInput(
    textFieldState: TextFieldState,
    onInputChanged: () -> Unit
) {
    // snapshotFlow の生データを、テスト可能な拡張関数に渡す
    textFieldState.asCommittedFlow()
        .collect {
            onInputChanged()
        }
}

private fun Flow<Pair<CharSequence, Boolean>>.filterCommitted(): Flow<CharSequence> {
    return this.distinctUntilChanged()
        .filter { !it.second } // 変換中 (isComposing) は通さない
        .map { it.first }
}

/**
 * snapshotFlow 部分を切り出し
 */
private fun TextFieldState.asCommittedFlow(): Flow<CharSequence> =
    snapshotFlow { text to (composition != null) }
        .filterCommitted()
