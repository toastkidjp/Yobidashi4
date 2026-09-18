package jp.toastkid.yobidashi4.domain.model.number

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull

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
    fun placeSolved() {
        numberPlaceGame.initialize(0)

        numberPlaceGame.place(0, 0, 1, {  })
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

    @Test
    fun serializationTest() {
        val game = NumberPlaceGame()
        val jsonString = Json.encodeToString(game)
        val decodedGame = Json.decodeFromString<NumberPlaceGame>(jsonString)

        assertNotNull(decodedGame)
    }

    @Test
    fun dataClassTest() {
        val game1 = NumberPlaceGame()
        val game2 = game1.copy()

        assertEquals(game1, game2)
        assertEquals(game1.hashCode(), game2.hashCode())
        assertNotNull(game1.toString())
    }

}