package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TableFormConverterTest {

    private val text ="""
7時30分 20.4
7時35分 20.6
7時40分 20.8
7時45分 20.9
7時50分 21.1
7時55分 21.3
8時0分 21.4
8時5分 21.5
8時10分 21.7
8時15分 21.9
8時20分 22.0
8時25分 22.1
8時35分 22.3
8時45分 22.4
8時55分 22.5
9時10分 22.8
""".trimIndent()

    @Test
    fun test() {
        TableFormConverter().invoke(text).trim().split("\n")
            .forEach {
                assertTrue(it.startsWith("| "))
                assertTrue(it.contains("分 | "))
            }
    }

    @Test
    fun notEndWithBreak() {
        assertEquals("| a | b", TableFormConverter().invoke("a b"))
    }

}