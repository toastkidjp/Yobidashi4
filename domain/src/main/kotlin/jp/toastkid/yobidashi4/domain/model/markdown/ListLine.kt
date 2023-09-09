package jp.toastkid.yobidashi4.domain.model.markdown

import jp.toastkid.yobidashi4.domain.model.slideshow.data.Line

data class ListLine(
    val list: List<String>,
    val ordered: Boolean = false,
    val taskList: Boolean = false
) : Line {

}