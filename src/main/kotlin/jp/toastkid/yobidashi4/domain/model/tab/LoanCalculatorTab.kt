package jp.toastkid.yobidashi4.domain.model.tab

class LoanCalculatorTab : Tab {
    override fun title(): String = "Loan Calculator"

    override fun closeable(): Boolean = true

    override fun iconPath(): String? {
        return "images/icon/ic_home.xml"
    }
}