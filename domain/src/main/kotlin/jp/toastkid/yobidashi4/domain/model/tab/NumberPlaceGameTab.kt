package jp.toastkid.yobidashi4.domain.model.tab

class NumberPlaceGameTab : Tab {

    override fun title(): String = "Number place"

    override fun closeable(): Boolean = true

    override fun iconPath(): String? {
        return "images/icon/ic_number_place.xml"
    }
}