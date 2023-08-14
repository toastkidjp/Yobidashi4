package jp.toastkid.yobidashi4.domain.model.article

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import java.time.LocalDate
import jp.toastkid.yobidashi4.domain.service.article.ArticleTemplate
import jp.toastkid.yobidashi4.domain.service.article.OffDayFinderService
import jp.toastkid.yobidashi4.domain.service.article.UserTemplateStreamReader
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ArticleTemplateTest {

    private lateinit var articleTemplate: ArticleTemplate

    @MockK
    private lateinit var offDayFinderService: OffDayFinderService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { offDayFinderService.invoke(any(), any(), any(), any(), any()) }.returns(false)

        articleTemplate = ArticleTemplate(offDayFinderService = offDayFinderService)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
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
        val content = ArticleTemplate(LocalDate.of(2023, 2, 23), offDayFinderService).invoke("test")
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
        val content = ArticleTemplate(LocalDate.of(2023, 2, 19), offDayFinderService).invoke("test")
        assertTrue(content.contains("test").not())
    }

}