package jp.toastkid.yobidashi4.domain.service.converter

class TsuboCountConverterService  : TwoStringConverterService {

    override fun title(): String {
        return "Tsubo counts(坪) <-> Square meter(㎡)"
    }

    override fun firstInputLabel(): String {
        return "Tsubo counts(坪)"
    }

    override fun secondInputLabel(): String {
        return "Square meter(㎡)"
    }

    override fun defaultFirstInputValue(): String {
        return "100"
    }

    override fun defaultSecondInputValue(): String {
        return "324.00"
    }

    override fun firstInputAction(input: String): String? {
        val d = input.toDoubleOrNull() ?: return null
        return String.format("%.2f", d * FACTOR)
    }

    override fun secondInputAction(input: String): String? {
        val d = input.toDoubleOrNull() ?: return null
        return String.format("%.2f", d / FACTOR)
    }

    companion object {

        private const val FACTOR = 3.24f

    }

}