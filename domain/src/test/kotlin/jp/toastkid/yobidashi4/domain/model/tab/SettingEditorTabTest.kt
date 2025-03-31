package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SettingEditorTabTest {

    @Test
    fun title() {
        assertEquals("Setting editor", SettingEditorTab().title())
    }

}