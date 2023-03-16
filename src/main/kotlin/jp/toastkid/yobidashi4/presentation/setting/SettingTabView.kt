package jp.toastkid.yobidashi4.presentation.setting

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.nio.file.Paths
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import kotlin.io.path.absolutePathString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


@Composable
fun SettingTabView() {
    val setting = remember { object : KoinComponent { val setting: Setting by inject() }.setting }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Box {
            val state = rememberScrollState()
            val horizontalScrollState = rememberScrollState()
            Column(
                modifier = Modifier.verticalScroll(state)
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        val path = Paths.get("user/article_template.txt")
                        Desktop.getDesktop().open(path.toFile())
                    }
                ) {
                    Text("Article template")
                }
                Divider(modifier = Modifier.padding(start = 16.dp))

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ) {
                    Text("Article Folder", modifier = Modifier.weight(0.4f))
                    Text(setting.articleFolderPath().absolutePathString(), modifier = Modifier.weight(0.6f))
                }
                Divider(modifier = Modifier.padding(start = 16.dp))
            }
            VerticalScrollbar(adapter = rememberScrollbarAdapter(state), modifier = Modifier.fillMaxHeight().align(
                Alignment.CenterEnd))
            HorizontalScrollbar(adapter = rememberScrollbarAdapter(horizontalScrollState), modifier = Modifier.fillMaxWidth().align(
                Alignment.BottomCenter))
        }
    }
}