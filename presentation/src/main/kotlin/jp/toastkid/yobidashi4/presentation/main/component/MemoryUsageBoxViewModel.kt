package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.runtime.mutableStateOf
import jp.toastkid.yobidashi4.domain.service.memory.MemoryUsage
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MemoryUsageBoxViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val memoryUsage = mutableStateOf(MemoryUsage())

    fun maxMemory() = "Max memory: ${memoryUsage.value.maxMemory()}"

    fun freeMemory() = "Free memory: ${memoryUsage.value.freeMemory()}"

    fun totalMemory() = "Total memory: ${memoryUsage.value.totalMemory()}"

    fun usedMemory() = "Used memory: ${memoryUsage.value.usedMemory()}"

    fun clickClose() {
        viewModel.switchMemoryUsageBox()
    }

    fun launchGarbageCollection() {
        System.gc()

        memoryUsage.value = MemoryUsage()
    }

}