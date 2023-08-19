package jp.toastkid.yobidashi4.presentation.slideshow

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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck
import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TextLine
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun Slideshow(deck: SlideDeck, modifier: Modifier) {
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
                    else -> false
                }
            }
    ) {
        HorizontalPager(
            pageCount = deck.slides.size,
            pageSize = PageSize.Fill,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) {
            val slide = deck.slides.get(pagerState.currentPage)

            Box(
                modifier = Modifier.padding(8.dp).fillMaxHeight().fillMaxHeight()
            ) {
                val columnModifier =
                if (slide.isFront()) {
                    Modifier.clickable {  }.wrapContentHeight().align(Alignment.Center)
                } else {
                    Modifier.clickable {  }.fillMaxHeight().align(Alignment.TopCenter)
                }

                Column(modifier = columnModifier) {
                    if (slide.hasTitle()) {
                        Text(slide.title(), fontSize = if (slide.isFront()) 48.sp else 36.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                    }

                    slide.lines().forEach { line ->
                        when (line) {
                            is TextLine ->
                                Text(line.text, modifier = Modifier.padding(bottom = 8.dp))
                            is ImageLine ->
                                Image(
                                    loadImageBitmap(Files.newInputStream(Path.of(line.source))),
                                    contentDescription = line.source
                                )
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}