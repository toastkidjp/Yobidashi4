package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.markdown.MarkdownView

@Composable
fun EditorTabView(tab: EditorTab) {
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
                        { status.value = it },
                        Modifier
                    )
                }

                val showPreview = remember { mutableStateOf(tab.showPreview()) }

                if (showPreview.value) {
                    MarkdownView(tab, Modifier.widthIn(max = 360.dp))
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
