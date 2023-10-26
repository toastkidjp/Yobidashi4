package jp.toastkid.yobidashi4.presentation.editor.style

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EditorThemeTest {

    private lateinit var editorTheme: EditorTheme

    @BeforeEach
    fun setUp() {
        editorTheme = EditorTheme()
    }

    @AfterEach
    fun tearDown() {
    }

    private val source = """
# Title
Description

## Table

| key | value
|:---|:---
| A | B

### List
- 1tab
- 2tab
                """

    @Test
    fun codeString() {
        val codeString = editorTheme.codeString(
            source,
            true
        )
        assertTrue(codeString.text.endsWith("[EOF]"))
        assertEquals(114, codeString.spanStyles.size)
    }

    @Test
    fun codeStringForLightCase() {
        val codeString = editorTheme.codeString(
            source,
            false
        )
        assertTrue(codeString.text.endsWith("[EOF]"))
        assertEquals(114, codeString.spanStyles.size)
    }

}