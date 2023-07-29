package jp.toastkid.yobidashi4.domain.service.converter

class TemperatureConverterService : TwoStringConverterService {

    /**
     * Fahrenheit temperature convert to Celsius temperature.
     * @param fahrenheit Fahrenheit temperature
     * @return Celsius temperature
     */
    private fun fToC(fahrenheit: Double): Double {
        return (fahrenheit - 32.0) / 1.8
    }

    /**
     * Celsius temperature convert to Fahrenheit temperature.
     * @param celsius Celsius temperature
     * @return Fahrenheit temperature
     */
    private fun cToF(celsius: Double): Double {
        return celsius * 1.8 + 32.0
    }

    override fun title(): String {
        return "Temperature"
    }

    override fun firstInputLabel(): String {
        return "Fahrenheit temperature"
    }

    override fun secondInputLabel(): String {
        return "Celsius temperature"
    }

    override fun defaultFirstInputValue(): String {
        return "77.0"
    }

    override fun defaultSecondInputValue(): String {
        return "25"
    }

    override fun firstInputAction(input: String): String? {
        return fToC(input.toDoubleOrNull() ?: 0.0).toString()
    }

    override fun secondInputAction(input: String): String? {
        return cToF(input.toDoubleOrNull() ?: 0.0).toString()
    }

}