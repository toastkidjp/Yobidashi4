package jp.toastkid.yobidashi4.domain.model.tab

interface ScrollableContentTab : Tab {

    fun scrollPosition(): Int

    fun withNewPosition(scrollPosition: Int): ScrollableContentTab

}