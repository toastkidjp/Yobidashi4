package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import java.net.URL
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.slideshow.Slide
import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TextLine
import jp.toastkid.yobidashi4.presentation.editor.EditorTheme
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun Slideshow(deck: SlideDeck, onEscapeKeyReleased: () -> Unit, onFullscreenKeyReleased: () -> Unit, modifier: Modifier) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp,
        modifier = modifier
            .onKeyEvent {
                if (it.type == KeyEventType.KeyDown) {
                    return@onKeyEvent false
                }
                when (it.key) {
                    Key.DirectionLeft -> {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(max(0, pagerState.currentPage - 1))
                        }
                        true
                    }
                    Key.DirectionRight -> {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(min(deck.slides.size - 1, pagerState.currentPage + 1))
                        }
                        true
                    }
                    Key.Escape -> {
                        onEscapeKeyReleased()
                        true
                    }
                    Key.F5 -> {
                        onFullscreenKeyReleased()
                        true
                    }
                    else -> false
                }
            }
    ) {
        Box(
            modifier = Modifier.padding(8.dp).fillMaxHeight().fillMaxHeight()
        ) {
            val backgroundUrl = deck.background
            if (backgroundUrl.isNotBlank()) {
                Image(
                    ImageIO.read(URL(backgroundUrl)).toComposeImageBitmap(),
                    "Background image",
                    modifier = Modifier.fillMaxSize()
                )
            }

            HorizontalPager(
                pageCount = deck.slides.size,
                pageSize = PageSize.Fill,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) {
                val slide = deck.slides.get(pagerState.currentPage)

                SlideView(slide, modifier)
            }

            if (deck.footerText.isNotBlank()) {
                Text(
                    deck.footerText,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }

            Text(
                "${pagerState.currentPage + 1} / ${deck.slides.size}",
                modifier = Modifier.align(Alignment.BottomEnd)
            )

            Slider(
                pagerState.currentPage.toFloat(),
                onValueChange = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it.toInt())
                    }
                },
                valueRange = 0f .. (deck.slides.size - 1).toFloat(),
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
private fun SlideView(
    slide: Slide,
    modifier: Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.padding(8.dp).fillMaxHeight().fillMaxHeight()
    ) {
        val backgroundUrl = slide.background()
        if (backgroundUrl.isNotBlank()) {
            Image(
                ImageIO.read(URL(backgroundUrl)).toComposeImageBitmap(),
                "Background image",
                modifier = Modifier.fillMaxSize()
            )
        }

        val columnModifier =
            if (slide.isFront()) {
                Modifier.clickable { }.wrapContentHeight().align(Alignment.Center)
            } else {
                Modifier.clickable { }.fillMaxHeight().align(Alignment.TopCenter)
            }

        Column(modifier = columnModifier) {
            if (slide.hasTitle()) {
                Text(
                    slide.title(),
                    fontSize = if (slide.isFront()) 48.sp else 36.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            slide.lines().forEach { line ->
                when (line) {
                    is TextLine ->
                        Text(line.text, modifier = Modifier.padding(bottom = 8.dp))

                    is ImageLine -> {
                        Image(
                            ImageIO.read(URL(line.source)).toComposeImageBitmap(),
                            contentDescription = line.source
                        )
                    }

                    is CodeBlockLine -> {
                        val verticalScrollState = rememberScrollState()
                        val horizontalScrollState = rememberScrollState()

                        val bringIntoViewRequester = remember { BringIntoViewRequester() }
                        val textLayoutResultState = remember { mutableStateOf<TextLayoutResult?>(null) }
                        val content = remember { mutableStateOf(TextFieldValue(line.code)) }

                        Box(modifier = Modifier.background(MaterialTheme.colors.surface)) {
                            BasicTextField(
                                value = content.value,
                                onValueChange = {
                                    content.value = it
                                },
                                visualTransformation = {
                                    val t = codeString(content.value.text)
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
                                                        fontSize = 28.sp,
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
                                    fontSize = 28.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 1.5.em
                                ),
                                modifier = modifier
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

                    is TableLine -> {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.surface)) {
                                println("line.header ${line.header}")
                                line.header.forEachIndexed { index, item ->
                                    if (index != 0) {
                                        Divider(modifier = Modifier.height(24.dp).width(1.dp).padding(vertical = 1.dp))
                                    }

                                    Text(
                                        item.toString(),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 16.dp)
                                    )
                                }
                            }

                            Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))

                            line.table.forEach { itemRow ->
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        itemRow.forEachIndexed { index, any ->
                                            if (index != 0) {
                                                Divider(
                                                    modifier = Modifier.height(24.dp).width(1.dp)
                                                        .padding(vertical = 1.dp)
                                                )
                                            }
                                            Text(
                                                any.toString(),
                                                fontSize = 24.sp,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(horizontal = 16.dp)
                                            )
                                        }
                                    }
                                    Divider(modifier = Modifier.padding(start = 16.dp, end = 4.dp))
                                }

                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}


private fun codeString(str: String) = buildAnnotatedString {
    val theme = EditorTheme.get()
    withStyle(theme.code.simple) {
        append(str)
        addStyle(theme.code.punctuation, str, ":")
        addStyle(theme.code.punctuation, str, "=")
        addStyle(theme.code.punctuation, str, "\"")
        addStyle(theme.code.punctuation, str, "[")
        addStyle(theme.code.punctuation, str, "]")
        addStyle(theme.code.punctuation, str, "{")
        addStyle(theme.code.punctuation, str, "}")
        addStyle(theme.code.punctuation, str, "(")
        addStyle(theme.code.punctuation, str, ")")
        addStyle(theme.code.punctuation, str, ",")
        addStyle(theme.code.keyword, str, "fun ")
        addStyle(theme.code.keyword, str, "---")
        addStyle(theme.code.keyword, str, "val ")
        addStyle(theme.code.keyword, str, "var ")
        addStyle(theme.code.keyword, str, "private ")
        addStyle(theme.code.keyword, str, "internal ")
        addStyle(theme.code.keyword, str, "for ")
        addStyle(theme.code.keyword, str, "expect ")
        addStyle(theme.code.keyword, str, "actual ")
        addStyle(theme.code.keyword, str, "import ")
        addStyle(theme.code.keyword, str, "package ")
        addStyle(theme.code.value, str, "true")
        addStyle(theme.code.value, str, "false")
        addStyle(theme.code.value, str, Regex("[0-9]*"))
        addStyle(theme.code.header, str, Regex("\\n#.*"))
        addStyle(theme.code.table, str, Regex("\\n\\|.*"))
        addStyle(theme.code.quote, str, Regex("\\n>.*"))
        addStyle(theme.code.quote, str, Regex("\\n-.*"))
        addStyle(theme.code.annotation, str, Regex("^@[a-zA-Z_]*"))
        addStyle(theme.code.comment, str, Regex("^\\s*//.*"))
    }
}

private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: String) {
    addStyle(style, text, Regex.fromLiteral(regexp))
}

private fun AnnotatedString.Builder.addStyle(style: SpanStyle, text: String, regexp: Regex) {
    for (result in regexp.findAll(text)) {
        addStyle(style, result.range.first, result.range.last + 1)
    }
}
