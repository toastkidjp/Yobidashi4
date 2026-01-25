/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.markdown.Subhead

@Composable
fun MarkdownSubhead(
    subheadings: List<Subhead>,
    function: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(modifier = modifier) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            subheadings.forEach {
                Text(
                    text = it.text(),
                    fontSize = it.fontSize().sp,
                    lineHeight = it.fontSize().sp,
                    modifier = Modifier.fillMaxWidth().clickable {
                        function(it.indexOf())
                    }
                )
            }
        }

        VerticalScrollbar(
            rememberScrollbarAdapter(scrollState),
            modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
        )
    }
}
