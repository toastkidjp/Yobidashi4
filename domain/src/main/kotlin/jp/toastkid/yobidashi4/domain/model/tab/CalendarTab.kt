package jp.toastkid.yobidashi4.domain.model.tab

class CalendarTab(): Tab {
    override fun title(): String {
        return "Calendar"
    }

    override fun closeable(): Boolean {
        return true
    }

    override fun iconPath(): String {
        return "images/icon/ic_calendar.xml"
    }

}