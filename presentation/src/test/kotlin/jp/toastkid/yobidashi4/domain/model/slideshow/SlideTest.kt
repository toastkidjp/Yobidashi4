package jp.toastkid.yobidashi4.domain.model.slideshow

import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SlideTest {

    private lateinit var slide: Slide

    @BeforeEach
    fun setUp() {
        slide = Slide()
        slide.setTitle("test")
        slide.setBackground("https://test.yahoo.co.jp/background.png")
        slide.addLine(ImageLine("https://test.yahoo.co.jp/test.png"))
        slide.addLine(ImageLine("https://test.yahoo.co.jp/test.jpg"))
        slide.addLine(ImageLine("https://test.yahoo.co.jp/test.webp"))
    }

    @Test
    fun title() {
        assertEquals("test", slide.title())
    }

    @Test
    fun extractImageUrls() {
        val imageUrls = slide.extractImageUrls()

        assertEquals(4, imageUrls.size)
    }
}