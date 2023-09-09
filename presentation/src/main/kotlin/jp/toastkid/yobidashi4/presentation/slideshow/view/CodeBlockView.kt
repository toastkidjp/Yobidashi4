package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine
import jp.toastkid.yobidashi4.presentation.text.code.CodeStringBuilder
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun CodeBlockView(line: CodeBlockLine, fontSize: TextUnit = 28.sp, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
    val content = remember { mutableStateOf(TextFieldValue(line.code)) }
    val codeStringBuilder = remember { CodeStringBuilder() }

    Box(modifier = modifier.background(MaterialTheme.colors.surface)) {
        BasicTextField(
            value = content.value,
            onValueChange = {
                content.value = it
            },
            visualTransformation = {
                val t = codeStringBuilder(content.value.text)
                TransformedText(t, OffsetMapping.Identity)
            },
            decorationBox = {
                Row {
                    Column(
                        modifier = Modifier
                            .scrollable(verticalScrollState, Orientation.Vertical)
                            .padding(end = 8.dp)
                            .wrapContentSize(unbounded = true)
                            .background(MaterialTheme.colors.surface.copy(alpha = 0.75f))
                    ) {
                        val textLines = content.value.text.split("\n")
                        val max = textLines.size
                        val length = max.toString().length
                        repeat(max) {
                            val lineNumberCount = it + 1
                            val fillCount = length - lineNumberCount.toString().length
                            val lineNumberText = with(StringBuilder()) {
                                repeat(fillCount) {
                                    append(" ")
                                }
                                append(lineNumberCount)
                            }.toString()
                            Box(
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text(
                                    lineNumberText,
                                    fontSize = fontSize,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.End,
                                    lineHeight = 1.5.em
                                )
                            }
                        }
                    }
                    it()
                }
            },
            onTextLayout = {
                textLayoutResultState.value = it
            },
            textStyle = TextStyle(
                fontSize = fontSize,
                fontFamily = FontFamily.Monospace,
                lineHeight = 1.5.em
            ),
            modifier = Modifier
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState)
                .onPointerEvent(PointerEventType.Scroll, PointerEventPass.Main) {
                    coroutineScope.launch {
                        verticalScrollState.scrollBy(it.changes.first().scrollDelta.y)
                    }
                }
                .bringIntoViewRequester(bringIntoViewRequester)
        )

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(verticalScrollState),
            modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
        )
        HorizontalScrollbar(
            adapter = rememberScrollbarAdapter(horizontalScrollState),
            modifier = Modifier.fillMaxWidth().align(
                Alignment.BottomCenter
            )
        )
    }
}
