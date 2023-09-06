package jp.toastkid.yobidashi4.domain.model.number

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NumberPlaceGameTest {

    private lateinit var numberPlaceGame: NumberPlaceGame

    @BeforeEach
    fun setUp() {
        numberPlaceGame = NumberPlaceGame()
        numberPlaceGame.initialize(20)
    }

    @Test
    fun masked() {
        assertFalse(numberPlaceGame.masked().fulfilled())
    }

    @Test
    fun place() {
        numberPlaceGame.place(0, 0, 1, {})
    }

    @Test
    fun setCorrect() {
        numberPlaceGame.setCorrect()

        (0 until 9).forEach { y ->
            (0 until 9).forEach { x ->
                assertEquals(
                    numberPlaceGame.pickCorrect(x, y),
                    numberPlaceGame.pickSolving(x, y)
                )
            }
        }
    }
}