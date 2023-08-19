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
}