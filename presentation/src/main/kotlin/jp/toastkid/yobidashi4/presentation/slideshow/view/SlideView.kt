package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.slideshow.Slide
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TextLine
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction

@Composable
fun SlideView(slide: Slide, loadImage: (String) -> ImageBitmap) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val keyboardScrollAction = remember { KeyboardScrollAction(scrollState) }
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier.padding(8.dp).fillMaxHeight()
    ) {
        val columnModifier =
            if (slide.isFront()) {
                Modifier.focusable(true).fillMaxWidth().wrapContentHeight().align(Alignment.Center)
            } else {
                Modifier.focusable(true).fillMaxHeight().align(Alignment.TopCenter)
            }

        val backgroundUrl = slide.background()
        if (backgroundUrl.isNotBlank()) {
            val bitmap = loadImage(backgroundUrl)
            Image(
                bitmap,
                "Background image",
                modifier = Modifier.fillMaxSize()
            )
        }

        val surfaceColor = MaterialTheme.colors.surface

        Column(
            horizontalAlignment = if (slide.isFront()) Alignment.CenterHorizontally else Alignment.Start,
            modifier = columnModifier
                .drawBehind {
                    if (slide.background().isNotEmpty()) {
                        drawRect(surfaceColor.copy(alpha = 0.75f))
                    }
                }
                .onKeyEvent {
                    return@onKeyEvent keyboardScrollAction.invoke(coroutineScope, it.key, it.isCtrlPressed)
                }
                .focusRequester(focusRequester)
                .clickable {
                    focusRequester.requestFocus()
                }
                .verticalScroll(scrollState)
        ) {
            if (slide.hasTitle()) {
                Text(
                    slide.title(),
                    fontSize = if (slide.isFront()) 48.sp else 36.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    lineHeight = if (slide.isFront()) 48.sp else 36.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            slide.lines().forEach { line ->
                when (line) {
                    is TextLine ->
                        Text(
                            line.text,
                            fontSize = 28.sp,
                            lineHeight = 36.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                    is ImageLine -> {
                        Image(
                            loadImage(line.source),
                            contentDescription = line.source
                        )
                    }

                    is CodeBlockLine -> CodeBlockView(line)

                    is TableLine -> {
                        TableLineView(line)
                    }

                    else -> Unit
                }
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
        )
    }
}
