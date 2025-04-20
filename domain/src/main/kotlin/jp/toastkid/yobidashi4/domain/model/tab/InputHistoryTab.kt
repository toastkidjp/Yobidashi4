package jp.toastkid.yobidashi4.domain.model.tab

class InputHistoryTab(
    val category: String,
    private val scrollPosition: Int = 0
) : ScrollableContentTab {

    override fun scrollPosition(): Int = scrollPosition

    override fun withNewPosition(scrollPosition: Int): ScrollableContentTab {
        return InputHistoryTab(this.category, scrollPosition)
    }

    override fun title(): String {
        return "Input history"
    }

}