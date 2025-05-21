package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.presentation.lib.text.KeywordHighlighter
import java.io.ByteArrayInputStream
import java.util.Base64
import javax.imageio.ImageIO

@Composable
internal fun MessageContent(
    text: String,
    base64Image: String? = null,
    modifier: Modifier
) {
    val keywordHighlighter = remember { KeywordHighlighter() }
    Column(modifier) {
        text.split("\n").forEach {
            val listLine = it.startsWith("* ")
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (listLine) {
                    Text(
                        "・ ",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Text(
                    keywordHighlighter(if (listLine) it.substring(2) else it),
                    fontSize = 16.sp
                )
            }
        }

        if (base64Image != null) {
            Image(
                ByteArrayInputStream(Base64.getDecoder().decode(base64Image)).use {
                    ImageIO.read(it)
                }.toPainter(),
                contentDescription = text
            )
        }
    }
}