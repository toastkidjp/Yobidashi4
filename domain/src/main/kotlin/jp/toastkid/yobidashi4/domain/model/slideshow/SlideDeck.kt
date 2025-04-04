package jp.toastkid.yobidashi4.domain.model.slideshow

data class SlideDeck(
    val slides: MutableList<Slide> = mutableListOf(),
    var background: String = "",
    var title: String = "",
    var footerText: String = "",
) {
    fun add(slide: Slide) {
        slides.add(slide)
    }

    fun extractImageUrls(): Set<String> {
        val imageUrls = mutableSetOf<String>()
        if (background.isNotBlank()) {
            imageUrls.add(background)
        }
        slides.map { it.extractImageUrls() }.flatten().forEach(imageUrls::add)
        return imageUrls
    }
}