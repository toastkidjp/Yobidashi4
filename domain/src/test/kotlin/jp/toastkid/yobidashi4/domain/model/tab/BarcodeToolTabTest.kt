package jp.toastkid.yobidashi4.domain.model.tab

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class BarcodeToolTabTest {

    @Test
    fun title() {
        assertNotNull(BarcodeToolTab().title())
    }

    @Test
    fun iconPath() {
        val iconPath = BarcodeToolTab().iconPath() ?: fail()

        assertTrue(iconPath.startsWith("images/icon/"))
        assertTrue(iconPath.endsWith(".xml"))
    }
}