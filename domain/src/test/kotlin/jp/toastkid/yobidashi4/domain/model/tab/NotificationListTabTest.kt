package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NotificationListTabTest {

    private lateinit var subject: NotificationListTab

    @BeforeEach
    fun setUp() {
        subject = NotificationListTab()
    }

    @Test
    fun title() {
        assertNotNull(subject.title())
    }
}