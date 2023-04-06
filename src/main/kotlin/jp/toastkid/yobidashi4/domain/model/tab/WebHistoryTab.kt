package jp.toastkid.yobidashi4.domain.model.tab

class WebHistoryTab : Tab {

    override fun title(): String {
        return "Web history"
    }

    override fun iconPath(): String? {
        return "images/icon/ic_history.xml"
    }

}