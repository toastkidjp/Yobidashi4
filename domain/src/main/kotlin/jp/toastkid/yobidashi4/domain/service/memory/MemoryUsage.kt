package jp.toastkid.yobidashi4.domain.service.memory

import java.text.NumberFormat

class MemoryUsage(private val runtime: Runtime = Runtime.getRuntime()) {

    fun maxMemory() = format(runtime.maxMemory())

    fun freeMemory() = format(runtime.freeMemory())

    fun totalMemory() = format(runtime.totalMemory())

    fun usedMemory() = format(runtime.totalMemory() - runtime.freeMemory())

    private fun format(l: Long) = "${NumberFormat.getInstance().format(l / MEGA_BYTE)}[MB]"

}

private val MEGA_BYTE = 1024 * 1024