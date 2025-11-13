/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.style

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.SpanStyle

@Immutable
data class EditorStyle(
    val regex: Regex,
    val lightStyle: SpanStyle,
    val darkStyle: SpanStyle
)