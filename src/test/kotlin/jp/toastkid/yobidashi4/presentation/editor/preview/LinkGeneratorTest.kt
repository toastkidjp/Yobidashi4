package jp.toastkid.yobidashi4.presentation.editor.preview

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LinkGeneratorTest {

    @InjectMockKs
    private lateinit var linkGenerator: LinkGenerator

    private val markdown = """
        1. [[『御伽草子』]] 4
        1. [[『存在の耐えられない軽さ』感想]] 4
        1. [[『あしながおじさん』感想]] 6
        1. [[『続あしながおじさん』感想]] 5
        1. [[『武士道』感想]] 6
        1. [[『阿Ｑ正伝』感想]] 5
        https://www.yahoo.co.jp
    """.trimIndent()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun invoke() {
        assertTrue(linkGenerator.invoke(markdown).contains("1. [『武士道』感想](https://internal/『武士道』感想)"))
    }
}