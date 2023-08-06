package jp.toastkid.yobidashi4.domain.model.tab

import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TextFileViewerTabTest {

    @Test
    fun iconPath() {
        assertTrue(TextFileViewerTab(Path.of("tomato.txt")).iconPath()?.contains("text") == true)
        assertTrue(TextFileViewerTab(Path.of("temporary/log1/tomato.txt")).iconPath()?.contains("text") == true)
        assertTrue(TextFileViewerTab(Path.of("temporary/logs/tomato.txt")).iconPath()?.contains("log") == true)
    }

}