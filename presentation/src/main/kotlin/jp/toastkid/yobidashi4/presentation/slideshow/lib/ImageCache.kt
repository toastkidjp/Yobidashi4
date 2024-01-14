package jp.toastkid.yobidashi4.presentation.slideshow.lib

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
