package jp.toastkid.yobidashi4.domain.service.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TextCountMessageFactoryTest {

    private lateinit var textCountMessageFactory: TextCountMessageFactory

    @BeforeEach
    fun setUp() {
        textCountMessageFactory = TextCountMessageFactory()
    }

    @Test
    fun invoke() {
        assertEquals("Count: 4 | Lines: 1", textCountMessageFactory.invoke("test"))
        assertEquals("Count: 10 | Lines: 2", textCountMessageFactory.invoke("""
test
test2
        """.trimIndent()))
        assertEquals("Count: 11 | Lines: 2", textCountMessageFactory.invoke("""
test
test2

        """.trimIndent()))
    }
}