package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.CommaInserter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommaInserterTest {

    @InjectMockKs
    private lateinit var commaInserter: CommaInserter

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun testInvoke() {
        assertEquals("100", commaInserter.invoke("100"))
        assertEquals("1,000", commaInserter.invoke("1000"))
        assertEquals("556,090", commaInserter.invoke("556090"))
        assertEquals("1,000,556,090", commaInserter.invoke("1000556090"))
        assertEquals("10,556,090", commaInserter.invoke("10556090"))
        assertEquals("1,556,090", commaInserter.invoke("1556090"))
        assertEquals("ora,nge", commaInserter.invoke("orange"))
    }

}