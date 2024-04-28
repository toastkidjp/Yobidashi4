package jp.toastkid.yobidashi4.presentation.editor.finder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FinderMessageFactoryTest {

    private lateinit var finderMessageFactory: FinderMessageFactory

    @BeforeEach
    fun setUp() {
        finderMessageFactory = FinderMessageFactory()
    }

    @Test
    fun invoke() {
        assertTrue(finderMessageFactory.invoke("", 2).isEmpty())
        assertTrue(finderMessageFactory.invoke(" ", 2).isEmpty())
        assertEquals("\"test\" was not found.", finderMessageFactory.invoke("test", -1))
        assertEquals("\"test\" was not found.", finderMessageFactory.invoke("test", 0))
        assertEquals("\"test\" was found. 1", finderMessageFactory.invoke("test", 1))
        assertEquals("\"test\" was found. 2", finderMessageFactory.invoke("test", 2))
    }

}