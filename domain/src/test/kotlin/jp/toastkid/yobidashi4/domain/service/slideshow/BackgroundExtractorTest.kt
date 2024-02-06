package jp.toastkid.yobidashi4.domain.service.slideshow

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class BackgroundExtractorTest {

    @Test
    fun elseCase() {
        assertNull(BackgroundExtractor().invoke(""))
    }

}