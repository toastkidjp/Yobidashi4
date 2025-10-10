/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
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
