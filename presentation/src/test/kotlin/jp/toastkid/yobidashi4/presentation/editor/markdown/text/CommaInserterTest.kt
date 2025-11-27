package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class CommaInserterTest {

    @InjectMockKs
    private lateinit var commaInserter: CommaInserter

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @ParameterizedTest
    @CsvSource(
        "null, null",
        "100, 100",
        "1000, 1_000",
        "556090, 556_090",
        "1000556090, 1_000_556_090",
        "10556090, 10_556_090",
        "1556090, 1_556_090",
        "orange, ora_nge",
        nullValues = ["null"]
    )
    fun testInvoke(input: String?, expected: String?) {
        assertEquals(expected?.replace("_", ","), commaInserter.invoke(input))
    }

}