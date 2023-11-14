package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.domain.service.markdown.MarkdownParser
import jp.toastkid.yobidashi4.presentation.markdown.MarkdownPreview

@Composable
internal fun EditorTabView(tab: EditorTab) {
    val status = remember { mutableStateOf("") }
    Surface(color = MaterialTheme.colors.surface.copy(alpha = 0.75f)) {
        Column {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    SimpleTextEditor(
                        tab,
                        { status.value = (if (tab.editable()) "" else "Not editable | ") + it },
                        Modifier
                    )
                }

                val showPreview = remember { mutableStateOf(tab.showPreview()) }

                if (showPreview.value) {
                    MarkdownPreview(MarkdownParser().invoke(tab.path), rememberScrollState(), Modifier.weight(1f))
                }

                LaunchedEffect(tab) {
                    tab.update().collect {
                        showPreview.value = tab.showPreview()
                    }
                }
            }
            StatusLabel(
                status.value,
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun StatusLabel(labelText: String?, modifier: Modifier) {
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = modifier.fillMaxWidth().padding(end = 8.dp)) {
        val statusLabel = labelText ?: ""
        Text(statusLabel, fontSize = 16.sp)
    }
}