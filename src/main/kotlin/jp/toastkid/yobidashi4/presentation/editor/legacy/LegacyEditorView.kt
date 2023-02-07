package jp.toastkid.yobidashi4.presentation.editor.legacy

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.RichTextThemeIntegration
import com.halilibo.richtext.ui.string.RichTextStringStyle
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.editor.preview.LinkBehaviorService
import jp.toastkid.yobidashi4.presentation.editor.preview.LinkGenerator
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LegacyEditorView(tab: EditorTab) {
    val editorFrame = remember { EditorFrame() }
    val focusRequester = remember { FocusRequester() }

    Row() {
        SwingPanel(
            background = Color.Transparent,
            factory = {
                editorFrame.getContent()
            },
            modifier = Modifier.fillMaxHeight().weight(0.5f).focusRequester(focusRequester)
        )
        if (tab.showPreview()) {
            MarkdownPreview(tab, Modifier.widthIn(max = 360.dp).wrapContentWidth(Alignment.Start))
        }
    }

    LaunchedEffect(tab.path) {
        focusRequester.requestFocus()
    }

    DisposableEffect(tab.path) {
        editorFrame.setText(tab.path, tab.getContent())
        editorFrame.setCaretPosition(tab.caretPosition())
        onDispose {
            MainViewModel.get().updateEditorContent(tab.path, editorFrame.currentText(), editorFrame.caretPosition(), false)
        }
    }
}

@Composable
private fun MarkdownPreview(tab: EditorTab, modifier: Modifier) {
    val linkGenerator = remember { LinkGenerator() }

    val scrollState = rememberScrollState()
    Box(modifier = modifier) {
        RichTextThemeIntegration(
            contentColor = { MaterialTheme.colors.onSurface }
        ) {
            RichText(
                style = RichTextStyle(
                    headingStyle = { level, textStyle ->
                        when (level) {
                            0 -> TextStyle(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                            1 -> TextStyle(
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                            2 -> TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            3 -> TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            4 -> TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textStyle.color.copy(alpha = .7F)
                            )
                            5 -> TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = textStyle.color.copy(alpha = .7f)
                            )
                            else -> textStyle
                        }
                    },
                    stringStyle = RichTextStringStyle(
                        linkStyle = SpanStyle(Color(0xFF000099))
                    )
                ),
                modifier = Modifier
                    .background(MaterialTheme.colors.surface.copy(alpha = 0.75f))
                    .padding(8.dp)
                    .verticalScroll(scrollState)
            ) {
                val content = linkGenerator.invoke(tab.getContent())
                Markdown(
                    content,
                    onLinkClicked = {
                        LinkBehaviorService().invoke(it)
                    }
                )
            }
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState), modifier = Modifier.fillMaxHeight().align(
                Alignment.CenterEnd
            )
        )
    }
}
