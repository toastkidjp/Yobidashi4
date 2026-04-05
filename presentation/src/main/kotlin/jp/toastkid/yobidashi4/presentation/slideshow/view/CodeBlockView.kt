package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_clipboard
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun CodeBlockView(line: CodeBlockLine, fontSize: TextUnit = 28.sp, modifier: Modifier = Modifier) {
    val viewModel = remember { CodeBlockViewModel() }

    val surfaceColor = MaterialTheme.colors.surface

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered = interactionSource.collectIsHoveredAsState()

    LaunchedEffect(isHovered.value) {
        if (isHovered.value) viewModel.cursorOn() else viewModel.cursorOff()
    }

    Surface(
        color = surfaceColor.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Box(
            modifier = modifier
                .drawBehind {
                    drawRect(surfaceColor)
                }
                .heightIn(max = viewModel.maxHeight(fontSize))
        ) {
            BasicTextField(
                state = viewModel.content(),
                onTextLayout = {
                    val multiParagraph = it.invoke()?.multiParagraph ?: return@BasicTextField
                    viewModel.setMultiParagraph(multiParagraph)
                },
                outputTransformation = viewModel.outputTransformation(),
                decorator = {
                    Row {
                        Column(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .wrapContentSize(unbounded = true)
                                .drawBehind {
                                    drawRect(surfaceColor.copy(alpha = 0.75f))
                                }
                        ) {
                            viewModel.lineNumberTexts().forEach { lineNumberText ->
                                Box(
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text(
                                        lineNumberText,
                                        fontSize = fontSize,
                                        fontFamily = FontFamily.Monospace,
                                        textAlign = TextAlign.End,
                                        style = TextStyle(
                                            lineHeight = 1.5.em,
                                            lineHeightStyle = LineHeightStyle(
                                                alignment = LineHeightStyle.Alignment.Center,
                                                trim = LineHeightStyle.Trim.None
                                            )
                                        )
                                    )
                                }
                            }
                        }
                        it()
                    }
                },
                textStyle = TextStyle(
                    color = MaterialTheme.colors.onSurface,
                    fontSize = fontSize,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 1.5.em,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    )
                ),
                scrollState = viewModel.verticalScrollState()
            )

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.verticalScrollState()),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )
            HorizontalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.horizontalScrollState()),
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
            )

            Icon(
                painterResource(Res.drawable.ic_clipboard),
                contentDescription = "Clip this code.",
                modifier = Modifier
                    .alpha(viewModel.alpha())
                    .clickable(onClick = viewModel::clipContent)
                    .hoverable(interactionSource)
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp)
            )
        }
    }

    LaunchedEffect(line.code) {
        viewModel.start(line.code)
    }
}
