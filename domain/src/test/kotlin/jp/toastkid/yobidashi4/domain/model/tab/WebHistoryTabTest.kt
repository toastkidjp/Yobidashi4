package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WebHistoryTabTest {

    private lateinit var subject: WebHistoryTab

    @BeforeEach
    fun setUp() {
        subject = WebHistoryTab()
    }

    @Test
    fun title() {
        assertNotNull(subject.title())
    }

    @Test
    fun iconPath() {
        assertNull(subject.iconPath())
    }

    @Test
    fun scrollPosition() {
        assertEquals(0, subject.scrollPosition())
    }

    @Test
    fun withNewPosition() {
        val withNewPosition = subject.withNewPosition(20)

        assertNotSame(withNewPosition, subject)
        assertEquals(20, withNewPosition.scrollPosition())
    }

}