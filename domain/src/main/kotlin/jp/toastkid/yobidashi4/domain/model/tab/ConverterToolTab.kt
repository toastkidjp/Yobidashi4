package jp.toastkid.yobidashi4.domain.model.tab

class ConverterToolTab : Tab {

    override fun title(): String {
        return "Converter"
    }

    override fun closeable(): Boolean {
        return true
    }

}