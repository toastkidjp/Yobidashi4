package jp.toastkid.yobidashi4.domain.service.converter

class DistanceConverterService : TwoStringConverterService {
    override fun title(): String {
        return "Minutes and Distance"
    }

    override fun firstInputLabel(): String {
        return "Minutes"
    }

    override fun secondInputLabel(): String {
        return "Distance(meter)"
    }

    override fun defaultFirstInputValue(): String {
        return "20"
    }

    override fun defaultSecondInputValue(): String {
        return "1600"
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

        private const val FACTOR = 80f

    }

}