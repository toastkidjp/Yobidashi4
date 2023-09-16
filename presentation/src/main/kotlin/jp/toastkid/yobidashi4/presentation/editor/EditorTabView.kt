package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab

@Composable
fun EditorTabView(tab: EditorTab) {
    Surface(color = MaterialTheme.colors.surface.copy(alpha = 0.5f)) {
        SimpleTextEditor(
            tab.path,
            Modifier
        )
    }
}
