package jp.toastkid.yobidashi4.domain.model.tab

class BarcodeToolTab : Tab {
    override fun title(): String {
        return "Barcode tool"
    }

    override fun iconPath(): String? {
        return "images/icon/ic_barcode.xml"
    }

}