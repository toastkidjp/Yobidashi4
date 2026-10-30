/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.memory

import java.text.NumberFormat

class MemoryUsage(private val runtime: Runtime = Runtime.getRuntime()) {

    fun maxMemory() = format(runtime.maxMemory())

    fun freeMemory() = format(runtime.freeMemory())

    fun totalMemory() = format(runtime.totalMemory())

    fun usedMemory() = format(runtime.totalMemory() - runtime.freeMemory())

    private fun format(l: Long) = "${NumberFormat.getInstance().format(l / MEGA_BYTE)}[MB]"

}

private const val MEGA_BYTE = 1024 * 1024