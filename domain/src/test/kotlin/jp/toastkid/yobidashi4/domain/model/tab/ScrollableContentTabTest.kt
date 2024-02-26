package jp.toastkid.yobidashi4.domain.model.tab

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScrollableContentTabTest {

    private lateinit var subject: ScrollableContentTab

    @BeforeEach
    fun setUp() {
        subject = object : ScrollableContentTab {
            override fun scrollPosition(): Int {
                return 0
            }

            override fun withNewPosition(scrollPosition: Int): ScrollableContentTab {
                return mockk()
            }

            override fun title(): String {
                return "test"
            }

        }
    }

    @Test
    fun test() {
        assertEquals(0, subject.scrollPosition())
        assertNotSame(subject.withNewPosition(10), subject)
        assertNotNull(subject.title())
    }

}