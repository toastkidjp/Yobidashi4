package jp.toastkid.yobidashi4.domain.service.slideshow

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ImageExtractorTest {

    @Test
    fun elseCase() {
        assertTrue(ImageExtractor().invoke(null).isEmpty())
        assertTrue(ImageExtractor().invoke("").isEmpty())
        assertTrue(ImageExtractor().invoke(" ").isEmpty())
    }

}