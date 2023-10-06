package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun StatusLabel(labelText: String?, modifier: Modifier) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = modifier.fillMaxWidth().padding(end = 8.dp)) {
        val statusLabel = labelText ?: ""
        Text(statusLabel, fontSize = 16.sp)
    }
}