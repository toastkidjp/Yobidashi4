package jp.toastkid.yobidashi4.domain.model.number

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NumberBoardTest {

    private lateinit var numberBoard: NumberBoard

    @BeforeEach
    fun setUp() {
        numberBoard = NumberBoard()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun fillZero() {
        assertTrue(numberBoard.fulfilled())
    }

    @Test
    fun masked() {
        // Given
        while (!numberBoard.fulfilled()) {
            numberBoard.placeRandom()
        }
        assertTrue(numberBoard.fulfilled())
        assertTrue(numberBoard.isCorrect(numberBoard))

        // When
        val masked = numberBoard.masked(20)

        // Then
        assertFalse(masked.fulfilled())
        assertFalse(numberBoard.isCorrect(masked))
    }

    @Test
    fun copyFrom() {
        numberBoard.placeRandom()
        val copied = NumberBoard()
        copied.copyFrom(numberBoard)

        numberBoard.rows().forEachIndexed { index, list ->
            assertEquals(list, copied.rows().get(index))
        }
    }

    @Test
    fun place() {
        numberBoard.place(0, 0, 2)

        val picked = numberBoard.pick(0, 0)

        assertEquals(2, picked)
    }

    @Test
    fun makeRandom() {
        assertFalse(NumberBoard.make().isCorrect(NumberBoard()))
    }

    @Test
    fun isCorrectOtherCase() {
        NumberBoard.make().isCorrect(null)
    }

}