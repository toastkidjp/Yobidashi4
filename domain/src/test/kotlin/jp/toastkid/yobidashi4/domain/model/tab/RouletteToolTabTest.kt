package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RouletteToolTabTest {

    private lateinit var subject: RouletteToolTab

    @BeforeEach
    fun setUp() {
        subject = RouletteToolTab()
    }

    @Test
    fun title() {
        assertTrue(subject.title().isNotBlank())
    }

    @Test
    fun iconPath() {
        assertNull(subject.iconPath())
    }

}