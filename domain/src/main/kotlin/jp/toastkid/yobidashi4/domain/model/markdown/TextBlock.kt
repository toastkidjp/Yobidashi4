package jp.toastkid.yobidashi4.domain.model.markdown

import jp.toastkid.yobidashi4.domain.model.slideshow.data.Line

data class TextBlock(
    val text: String,
    val level: Int = -1,
    val quote: Boolean = false
) : Line {

    fun fontSize() = when (level) {
        1 -> 36
        2 -> 26
        3 -> 22
        4 -> 20
        5 -> 18
        6 -> 12
        else -> 14
    }

}