package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TextFileViewerTabTest {

    @Test
    fun test() {
        val path = Path.of("tomato.txt")
        val tab = TextFileViewerTab(path)

        assertNotNull(tab.title())
        assertTrue(tab.closeable())
        assertSame(path, tab.path())
        assertNull(tab.iconPath())
    }

}