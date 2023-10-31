package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
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
        assertTrue(subject.iconPath()?.startsWith("images/icon") ?: false)
    }
}