package jp.toastkid.yobidashi4.presentation.editor.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.ClassicColorPicker
import java.awt.GraphicsEnvironment

@Composable
internal fun EditorSettingComponent(modifier: Modifier) {
    val viewModel = remember { EditorSettingViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = modifier.padding(8.dp)
    ) {
        Column {
            Text("Background color")

            ClassicColorPicker(
                color = viewModel.currentBackgroundColor(),
                onColorChanged = { hsvColor ->
                    viewModel.onBackgroundColorChanged(hsvColor)
                },
                modifier = Modifier.height(200.dp)
            )

            Text("Font color")
            ClassicColorPicker(
                color = viewModel.currentFontColor(),
                onColorChanged = { hsvColor ->
                    viewModel.onFontColorChanged(hsvColor)
                },
                modifier = Modifier.height(200.dp)
            )

            Button(onClick = viewModel::commit,
                colors = ButtonDefaults.buttonColors(backgroundColor = viewModel.currentBackgroundColor(), contentColor = viewModel.currentFontColor())
            ) {
                Text("Commit colors")
            }

            EditorSettingDropdown(
                GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames.toList(),
                "Font family: ${viewModel.editorFontFamily()}",
                viewModel.isOpenFontFamily(),
                {
                    viewModel.closeFontFamily()
                },
                {
                    viewModel.setEditorFontFamily(it.toString())
                },
                Modifier.clickable(onClick = viewModel::openFontFamily)
            )

            EditorSettingDropdown(
                (12 .. 24).toList(),
                "Font Size: ${viewModel.editorFontSize()}",
                viewModel.isOpenFontSize(),
                {
                    viewModel.closeFontSize()
                },
                {
                    viewModel.setEditorFontSize(it)
                },
                Modifier.clickable { viewModel.openFontSize() }
            )

            Text("Reset color setting", modifier = Modifier.clickable {
                viewModel.reset()
            })
        }
    }
}

@Composable
private fun EditorSettingDropdown(
    items: Collection<Any>,
    displayText: String,
    open: Boolean,
    onClose: () -> Unit,
    onSelectValue: (Any) -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
    ) {
        Text(displayText)
        DropdownMenu(
            open,
            onDismissRequest = onClose
        ) {
            items.forEach {
                DropdownMenuItem(
                    onClick = {
                        onClose()
                        onSelectValue(it)
                    }
                ) {
                    Text("$it")
                }
            }
        }
    }
}