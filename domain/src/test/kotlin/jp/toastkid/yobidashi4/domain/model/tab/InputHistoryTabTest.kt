package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InputHistoryTabTest {

    private lateinit var subject: InputHistoryTab

    @BeforeEach
    fun setUp() {
        subject = InputHistoryTab("test")
    }

    @Test
    fun withNewPosition() {
        val newTab = subject.withNewPosition(12)
        if (newTab !is InputHistoryTab) {
            throw RuntimeException()
        }

        assertEquals(subject.category, newTab.category)
        assertEquals(12, newTab.scrollPosition())
    }

    @Test
    fun title() {
        assertEquals("Input history", subject.title())
    }

}