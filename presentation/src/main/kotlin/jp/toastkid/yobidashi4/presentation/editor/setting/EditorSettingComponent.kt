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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.godaddy.android.colorpicker.ClassicColorPicker
import java.awt.GraphicsEnvironment
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
internal fun EditorSettingComponent(modifier: Modifier) {
    val setting = object : KoinComponent { val s: Setting by inject() }.s

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = modifier.padding(8.dp)
    ) {
        Column {
            Text("Background color")
            val currentBackgroundColor = remember {
                mutableStateOf(
                    setting.editorBackgroundColor()?.let {
                        Color(it.red, it.green, it.blue, it.alpha)
                    } ?: Color.LightGray
                )
            }
            val currentFontColor = remember {
                mutableStateOf(
                    setting.editorForegroundColor()?.let {
                        Color(it.red, it.green, it.blue, it.alpha)
                    } ?: Color.Black
                )
            }
            ClassicColorPicker(
                color = currentBackgroundColor.value,
                onColorChanged = { hsvColor ->
                    currentBackgroundColor.value = hsvColor.toColor()
                },
                modifier = Modifier.height(200.dp)
            )

            Text("Font color")
            ClassicColorPicker(
                color = currentFontColor.value,
                onColorChanged = { hsvColor ->
                    currentFontColor.value = hsvColor.toColor()
                },
                modifier = Modifier.height(200.dp)
            )

            Button(onClick = {
                setting.setEditorBackgroundColor(
                    java.awt.Color(
                        currentBackgroundColor.value.red,
                        currentBackgroundColor.value.green,
                        currentBackgroundColor.value.blue,
                        currentBackgroundColor.value.alpha
                    )
                )
                setting.setEditorForegroundColor(
                    java.awt.Color(
                        currentFontColor.value.red,
                        currentFontColor.value.green,
                        currentFontColor.value.blue,
                        currentFontColor.value.alpha
                    )
                )
            },
                colors = ButtonDefaults.buttonColors(backgroundColor = currentBackgroundColor.value, contentColor = currentFontColor.value)
            ) {
                Text("Commit colors")
            }

            val openFontFamily = remember { mutableStateOf(false) }
            EditorSettingDropdown(
                GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames.toList(),
                "Font family: ${setting.editorFontFamily()}",
                openFontFamily.value,
                {
                    openFontFamily.value = it
                },
                {
                    setting.setEditorFontFamily(it.toString())
                }
            )

            val openFontSize = remember { mutableStateOf(false) }
            EditorSettingDropdown(
                (12 .. 24).toList(),
                "Font Size: ${setting.editorFontSize()}",
                openFontSize.value,
                {
                    openFontSize.value = it
                },
                {
                    if (it is Int) {
                        setting.setEditorFontSize(it)
                    }
                }
            )

            Text("Reset color setting", modifier = Modifier.clickable {
                setting.resetEditorColorSetting()
            })
        }
    }
}

@Composable
private fun EditorSettingDropdown(
    items: Collection<Any>,
    displayText: String,
    open: Boolean,
    onChangeState: (Boolean) -> Unit,
    onSelectValue: (Any) -> Unit
) {
    Box(
        modifier = Modifier.clickable {
            onChangeState(true)
        }
    ) {
        Text(displayText)
        DropdownMenu(
            open,
            onDismissRequest = {  onChangeState(false) }
        ) {
            items.forEach {
                DropdownMenuItem(
                    onClick = {
                        onChangeState(false)
                        onSelectValue(it)
                    }
                ) {
                    Text("$it")
                }
            }
        }
    }
}