package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class BarcodeToolTabTest {

    @Test
    fun title() {
        assertNotNull(BarcodeToolTab().title())
    }

    @Test
    fun iconPath() {
        assertNull(BarcodeToolTab().iconPath())
    }
}