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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MemoryUsageBox() {
    val viewModel = remember { MemoryUsageBoxViewModel() }

    Surface(
        modifier = Modifier.wrapContentHeight().fillMaxWidth(),
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Box {
            Row(modifier = Modifier.padding(start = 40.dp)) {
                Column {
                    Text(viewModel.usedMemory(), modifier = Modifier.padding(8.dp))
                    Text(viewModel.freeMemory(), modifier = Modifier.padding(8.dp))
                }
                Column {
                    Text(viewModel.totalMemory(), modifier = Modifier.padding(8.dp))
                    Text(viewModel.maxMemory(), modifier = Modifier.padding(8.dp))
                }
                Button(onClick = {
                    viewModel.launchGarbageCollection()
                }) {
                    Text("Launch GC", modifier = Modifier.padding(8.dp))
                }
            }
            Text("x", modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp)
                .background(MaterialTheme.colors.surface.copy(alpha = 0.2f))
                .clickable(onClick = viewModel::clickClose)
                .padding(8.dp)
            )
        }
    }
}