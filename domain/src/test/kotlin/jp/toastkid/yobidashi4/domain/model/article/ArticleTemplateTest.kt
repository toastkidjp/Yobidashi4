package jp.toastkid.yobidashi4.domain.model.article

import io.mockk.every
import io.mockk.mockkConstructor
import java.time.LocalDate
import jp.toastkid.yobidashi4.domain.service.article.ArticleTemplate
import jp.toastkid.yobidashi4.domain.service.article.UserTemplateStreamReader
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ArticleTemplateTest {

    private lateinit var articleTemplate: ArticleTemplate

    @BeforeEach
    fun setUp() {
        articleTemplate = ArticleTemplate()
    }

    @Test
    fun test() {
        val content = articleTemplate.invoke("test")
        assertTrue(content.startsWith("# test"))
    }

    @Test
    fun testContainsStockDay() {
        mockkConstructor(UserTemplateStreamReader::class)
        every { anyConstructed<UserTemplateStreamReader>().invoke() }.returns("""
            {{stock}}
test
            {{/stock}}
        """.trimIndent().byteInputStream())
        val content = ArticleTemplate(LocalDate.of(2023, 2, 23)).invoke("test")
        assertTrue(content.contains("test"))
    }

    @Test
    fun testContainsStockDayButOnSunday() {
        mockkConstructor(UserTemplateStreamReader::class)
        every { anyConstructed<UserTemplateStreamReader>().invoke() }.returns("""
            {{stock}}
test
            {{/stock}}
        """.trimIndent().byteInputStream())
        val content = ArticleTemplate(LocalDate.of(2023, 2, 19)).invoke("test")
        assertTrue(content.contains("test").not())
    }

}