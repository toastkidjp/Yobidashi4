/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.content.tab

import androidx.compose.runtime.Composable
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import kotlin.reflect.KClass

class TabContentRegistry private constructor(
    private val renderers: Map<KClass<out Tab>, @Composable (Tab) -> Unit>
) {

    @Composable
    fun Render(tab: Tab?) {
        if (tab == null) return
        val renderer = renderers[tab::class]
        renderer?.invoke(tab)
    }

    class Builder {
        private val map = mutableMapOf<KClass<out Tab>, @Composable (Tab) -> Unit>()

        fun <T : Tab> register(
            kClass: KClass<T>,
            content: @Composable (T) -> Unit
        ) = apply {
            map[kClass] = { content(it as T) }
        }

        fun build() = TabContentRegistry(map)

    }

}
