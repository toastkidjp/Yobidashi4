package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck
import jp.toastkid.yobidashi4.presentation.slideshow.lib.ImageCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.URI
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min

class SlideshowViewModel {

    private val imageCache = ImageCache()

    private val sliderVisibility = mutableStateOf(false)

    private val sliderState = mutableStateOf(0f)

    private val onEscapeKeyReleased = AtomicReference(EMPTY_ACTION)

    private val onFullscreenKeyReleased = AtomicReference(EMPTY_ACTION)

    private val maxSize = AtomicInteger(-1)

    private val focusRequester = FocusRequester()

    fun launch(deck: SlideDeck, onEscapeKeyReleased: () -> Unit, onFullscreenKeyReleased: () -> Unit) {
        this.onEscapeKeyReleased.set(onEscapeKeyReleased)
        this.onFullscreenKeyReleased.set(onFullscreenKeyReleased)
        deck.extractImageUrls().forEach {
            imageCache.get(it)
        }
        maxSize.set(deck.slides.size)
    }

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
                    pagerState.animateScrollToPage(min(maxSize.get() - 1, pagerState.currentPage + 1))
                }
                true
            }
            Key.Enter -> {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(min(maxSize.get() - 1, pagerState.currentPage + 1))
                }
                true
            }
            Key.Escape -> {
                onEscapeKeyReleased.get().invoke()
                true
            }
            Key.F5 -> {
                onFullscreenKeyReleased.get().invoke()
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

        val bitmap = ImageIO.read(URI(backgroundUrl).toURL()).toComposeImageBitmap()
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

    fun sliderValue() = sliderState.value

    fun setSliderValue(newValue: Float) {
        sliderState.value = newValue
    }

    fun focusRequester() = focusRequester

    fun requestFocus() {
        focusRequester().requestFocus()
    }

}

private val EMPTY_ACTION: () -> Unit = {}
