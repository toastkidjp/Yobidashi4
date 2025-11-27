package jp.toastkid.yobidashi4.presentation.editor.finder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class FinderMessageFactoryTest {

    private lateinit var finderMessageFactory: FinderMessageFactory

    @BeforeEach
    fun setUp() {
        finderMessageFactory = FinderMessageFactory()
    }

    @ParameterizedTest
    @CsvSource(
        "'', -1, ''",
        "'', 2, ''",
        "' ', 2, ''",
        "test, -1, \"test\" was not found.",
        "test, 0, \"test\" was not found.",
        "test, 1, \"test\" was found. 1",
        "test, 2, \"test\" was found. 2",
    )
    fun invokeWith(targetText: String, foundCount: Int, expected: String) {
        assertEquals(expected, finderMessageFactory.invoke(targetText, foundCount))
    }

}