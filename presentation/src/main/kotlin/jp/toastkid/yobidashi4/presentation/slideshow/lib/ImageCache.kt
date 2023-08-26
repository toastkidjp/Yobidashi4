package jp.toastkid.yobidashi4.presentation.slideshow.lib

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.ImageBitmap

class ImageCache {

    private val map = mutableMapOf<String, ImageBitmap>()

    fun put(key: String, imageBitmap: ImageBitmap) {
        map.put(key, imageBitmap)
    }

    fun get(key: String): ImageBitmap? {
        return map.get(key)
    }

}

@Composable
fun rememberImageCache(): ImageCache {
    return rememberSaveable {
        ImageCache()
    }
}