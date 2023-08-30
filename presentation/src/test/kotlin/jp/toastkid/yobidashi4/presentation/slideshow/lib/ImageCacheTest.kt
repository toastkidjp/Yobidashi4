package jp.toastkid.yobidashi4.presentation.slideshow.lib

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ImageCacheTest {

    private lateinit var imageCache: ImageCache

    @BeforeEach
    fun setUp() {
        imageCache = ImageCache()
    }

    @Test
    fun test() {
        assertNull(imageCache.get("not-extists"))

        imageCache.put("test", mockk())
        assertNotNull(imageCache.get("test"))
    }

}