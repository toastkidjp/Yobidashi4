/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.markdown

import jp.toastkid.yobidashi4.domain.model.slideshow.data.Line

data class Markdown(
    private val title: String,
    private val lines: MutableList<Line> = mutableListOf(),
    private val subheadings: MutableList<Subhead> = mutableListOf(),
) {

    fun title() = title

    fun add(line: Line) {
        lines.add(line)

        if (line is TextBlock && line.level != -1) {
            this.subheadings.add(
                Subhead(
                    line.text,
                    line.fontSize(),
                    lines.size
                )
            )
        }
    }

    fun addAll(lines: List<Line>) {
        this.lines.addAll(lines)
    }

    fun lines(): List<Line> = lines

    fun subheadings(): List<Subhead> = subheadings

}