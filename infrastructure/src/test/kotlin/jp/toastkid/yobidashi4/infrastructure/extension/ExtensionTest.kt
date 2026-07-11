package jp.toastkid.yobidashi4.infrastructure.extension

import okio.Path.Companion.toPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExtensionTest {

    @Test
    fun getExtension() {
        assertTrue("test".toPath().extension.isEmpty())
        assertTrue("test.".toPath().extension.isEmpty())
        assertEquals("txt", "test.txt".toPath().extension)
        assertEquals("exe", "test.txt.exe".toPath().extension)
        assertTrue(".".toPath().extension.isEmpty())
        assertTrue("..".toPath().extension.isEmpty())
    }

}