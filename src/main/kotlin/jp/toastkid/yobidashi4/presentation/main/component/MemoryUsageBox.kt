package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.service.memory.MemoryUsage
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun MemoryUsageBox() {
    Surface(
        modifier = Modifier.wrapContentHeight().fillMaxWidth(),
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        val memoryUsage = remember { mutableStateOf(MemoryUsage()) }
        Box {
            Row {
                Column {
                    Text("Used memory: ${memoryUsage.value.usedMemory()}", modifier = Modifier.padding(8.dp))
                    Text("Free memory: ${memoryUsage.value.freeMemory()}", modifier = Modifier.padding(8.dp))
                    Text("Total memory: ${memoryUsage.value.totalMemory()}", modifier = Modifier.padding(8.dp))
                    Text("Max memory: ${memoryUsage.value.maxMemory()}", modifier = Modifier.padding(8.dp))
                }
                Button(onClick = {
                    System.gc()
                    memoryUsage.value = MemoryUsage()
                }) {
                    Text("Launch GC", modifier = Modifier.padding(8.dp))
                }
            }
            Text("x", modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(start = 4.dp)
                .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                .clickable { object : KoinComponent { val viewModel: MainViewModel by inject() }.viewModel.switchMemoryUsageBox() }
                .padding(8.dp)
            )
        }
    }
}