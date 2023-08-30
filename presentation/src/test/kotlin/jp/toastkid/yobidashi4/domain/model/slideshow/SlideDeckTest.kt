package jp.toastkid.yobidashi4.domain.model.slideshow

import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SlideDeckTest {

    private lateinit var slides: SlideDeck

    @BeforeEach
    fun setUp() {
        slides = SlideDeck()
        slides.background = "https://test.yahoo.co.jp/background.png"

        val slide = Slide()
        slide.setBackground("https://test.yahoo.co.jp/background2.png")
        slide.addLine(ImageLine("https://test.yahoo.co.jp/test.png"))
        slide.addLine(ImageLine("https://test.yahoo.co.jp/test.webp"))
        slides.slides.add(slide)
    }

    @Test
    fun extractImageUrls() {
        val imageUrls = slides.extractImageUrls()

        Assertions.assertEquals(4, imageUrls.size)
    }

}