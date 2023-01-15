package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class ContrastRatioCalculatorService {

    operator fun invoke(color1: Color, color2: Color): Float {
        val lum1 = calculateLuminance(color1)
        val lum2 = calculateLuminance(color2)
        val brighter = max(lum1, lum2)
        val darker = min(lum1, lum2)
        return (brighter + 0.05f) / (darker + 0.05f)
    }

    private fun calculateLuminance(color: Color): Float {
        val colorValueArray = arrayOf(color.red, color.green, color.blue).map {
            var v = it.toFloat()
            v /= 255
            if (v <= 0.03928f) v / 12.92f else ((v + 0.055) / 1.055).pow(2.4).toFloat()
        }
        return colorValueArray[0] * 0.2126f + colorValueArray[1] * 0.7152f + colorValueArray[2] * 0.0722f
    }

}