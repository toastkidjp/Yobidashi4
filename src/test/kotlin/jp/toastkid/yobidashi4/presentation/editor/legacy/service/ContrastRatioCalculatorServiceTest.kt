package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.awt.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ContrastRatioCalculatorServiceTest {

    private lateinit var contrastRatioCalculatorService: ContrastRatioCalculatorService

    @BeforeEach
    fun setUp() {
        contrastRatioCalculatorService = ContrastRatioCalculatorService()
    }

    @Test
    fun test() {
        assertEquals(1.0738392f, contrastRatioCalculatorService.invoke(Color.WHITE, Color.YELLOW))
        assertEquals(8.592471f, contrastRatioCalculatorService.invoke(Color.WHITE, Color.BLUE))
    }

}