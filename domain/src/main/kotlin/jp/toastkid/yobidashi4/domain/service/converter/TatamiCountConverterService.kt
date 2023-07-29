package jp.toastkid.yobidashi4.domain.service.converter

class TatamiCountConverterService : TwoStringConverterService {

    override fun title(): String {
        return "Tatami counts(畳) <-> Square meter(㎡)"
    }

    override fun firstInputLabel(): String {
        return "Tatami counts(畳)"
    }

    override fun secondInputLabel(): String {
        return "Square meter(㎡)"
    }

    override fun defaultFirstInputValue(): String {
        return "8"
    }

    override fun defaultSecondInputValue(): String {
        return "12.96"
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

        private const val FACTOR = 1.62f

    }

}