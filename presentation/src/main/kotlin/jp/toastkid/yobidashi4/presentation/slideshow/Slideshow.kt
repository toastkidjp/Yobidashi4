package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.URL
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.slideshow.Slide
import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TextLine
import jp.toastkid.yobidashi4.presentation.slideshow.lib.ImageCache
import jp.toastkid.yobidashi4.presentation.slideshow.lib.rememberImageCache
import jp.toastkid.yobidashi4.presentation.slideshow.view.CodeBlockView
import jp.toastkid.yobidashi4.presentation.slideshow.view.TableLineView
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun Slideshow(deck: SlideDeck, onEscapeKeyReleased: () -> Unit, onFullscreenKeyReleased: () -> Unit, modifier: Modifier) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val imageCache = rememberImageCache()

    LaunchedEffect(deck) {
        CoroutineScope(Dispatchers.IO).launch {
            deck.extractImageUrls().forEach {
                imageCache.get(it)
            }
        }
    }

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
                val bitmap = loadImage(imageCache, backgroundUrl)
                Image(
                    bitmap,
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

                SlideView(slide)
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

            val sliderVisibility = remember { mutableStateOf(false) }

            val alpha = animateFloatAsState(if (sliderVisibility.value) 1f else 0f)
            Slider(
                pagerState.currentPage.toFloat(),
                onValueChange = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it.toInt())
                    }
                },
                valueRange = 0f .. (deck.slides.size - 1).toFloat(),
                modifier = Modifier.align(Alignment.BottomCenter).alpha(alpha.value)
                    .onPointerEvent(PointerEventType.Enter) {
                        sliderVisibility.value = true
                    }
                    .onPointerEvent(PointerEventType.Exit) {
                        sliderVisibility.value = false
                    }
            )
        }
    }
}

@Composable
private fun SlideView(slide: Slide) {
    val imageCache = rememberImageCache()
    Box(
        modifier = Modifier.padding(8.dp).fillMaxHeight().fillMaxHeight()
    ) {
        val backgroundUrl = slide.background()
        if (backgroundUrl.isNotBlank()) {
            val bitmap = loadImage(imageCache, backgroundUrl)
            Image(
                bitmap,
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
                        Text(line.text, fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))

                    is ImageLine -> {
                        Image(
                            loadImage(imageCache, line.source),
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
    }
}

private fun loadImage(
    imageCache: ImageCache,
    backgroundUrl: String
): ImageBitmap {
    val fromCache = imageCache.get(backgroundUrl)
    if (fromCache != null) {
        return fromCache
    }

    val bitmap = ImageIO.read(URL(backgroundUrl)).toComposeImageBitmap()
    imageCache.put(backgroundUrl, bitmap)
    return bitmap
}