package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import java.net.URL
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck
import jp.toastkid.yobidashi4.presentation.slideshow.lib.ImageCache
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SlideshowViewModel {

    private val imageCache = ImageCache()

    private val sliderVisibility = mutableStateOf(false)

    private var onEscapeKeyReleased: (() -> Unit)? = null

    private var onFullscreenKeyReleased: (() -> Unit)? = null

    private var maxSize = -1

    fun launch(deck: SlideDeck, onEscapeKeyReleased: () -> Unit, onFullscreenKeyReleased: () -> Unit) {
        this.onEscapeKeyReleased = onEscapeKeyReleased
        this.onFullscreenKeyReleased = onFullscreenKeyReleased
        deck.extractImageUrls().forEach {
            imageCache.get(it)
        }
        maxSize = deck.slides.size
    }

    @OptIn(ExperimentalFoundationApi::class)
    fun onKeyEvent(coroutineScope: CoroutineScope, it: KeyEvent, pagerState: PagerState): Boolean {
        if (it.type == KeyEventType.KeyDown) {
            return@onKeyEvent false
        }
        return when (it.key) {
            Key.DirectionLeft -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(max(0, pagerState.currentPage - 1))
                }
                true
            }
            Key.DirectionRight -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(min(maxSize - 1, pagerState.currentPage + 1))
                }
                true
            }
            Key.Escape -> {
                onEscapeKeyReleased?.invoke()
                true
            }
            Key.F5 -> {
                onFullscreenKeyReleased?.invoke()
                true
            }
            else -> false
        }
    }

    fun loadImage(backgroundUrl: String): ImageBitmap {
        val fromCache = imageCache.get(backgroundUrl)
        if (fromCache != null) {
            return fromCache
        }

        val bitmap = ImageIO.read(URL(backgroundUrl)).toComposeImageBitmap()
        imageCache.put(backgroundUrl, bitmap)
        return bitmap
    }

    fun sliderAlpha(): Float {
        return if (sliderVisibility.value) 1f else 0f
    }

    fun showSlider() {
        sliderVisibility.value = true
    }

    fun hideSlider() {
        sliderVisibility.value = false
    }

    fun setSliderVisibility(b: Boolean) {
        sliderVisibility.value = b
    }

}