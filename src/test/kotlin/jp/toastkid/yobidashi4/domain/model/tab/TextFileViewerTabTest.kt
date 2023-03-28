package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TextFileViewerTabTest {

    @Test
    fun iconPath() {
        assertTrue(TextFileViewerTab(Paths.get("tomato.txt")).iconPath()?.contains("text") == true)
        assertTrue(TextFileViewerTab(Paths.get("data/log1/tomato.txt")).iconPath()?.contains("text") == true)
        assertTrue(TextFileViewerTab(Paths.get("data/logs/tomato.txt")).iconPath()?.contains("log") == true)
    }
}