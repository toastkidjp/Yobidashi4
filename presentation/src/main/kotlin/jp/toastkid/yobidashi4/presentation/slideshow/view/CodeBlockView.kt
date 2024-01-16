package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun CodeBlockView(line: CodeBlockLine, fontSize: TextUnit = 28.sp, modifier: Modifier = Modifier) {
    val viewModel = remember { CodeBlockViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Box(modifier = modifier.background(MaterialTheme.colors.surface).heightIn(max = viewModel.maxHeight(fontSize))) {
            BasicTextField(
                value = viewModel.content(),
                onValueChange = {
                    viewModel.onValueChange(it)
                },
                visualTransformation = {
                    viewModel.transform(it)
                },
                decorationBox = {
                    Row {
                        Column(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .wrapContentSize(unbounded = true)
                                .background(MaterialTheme.colors.surface.copy(alpha = 0.75f))
                        ) {
                            viewModel.lineNumberTexts().forEach {
                                Box(
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text(
                                        it,
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
                textStyle = TextStyle(
                    color = MaterialTheme.colors.onSurface,
                    fontSize = fontSize,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 1.5.em
                ),
                scrollState = viewModel.verticalScrollState()
            )

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.verticalScrollState()),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )
            HorizontalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.horizontalScrollState()),
                modifier = Modifier.fillMaxWidth().align(
                    Alignment.BottomCenter
                )
            )
        }
    }

    LaunchedEffect(line.code) {
        viewModel.start(line.code)
    }
}
