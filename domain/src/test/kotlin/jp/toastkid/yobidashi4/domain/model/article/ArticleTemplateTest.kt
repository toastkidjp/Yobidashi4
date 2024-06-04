package jp.toastkid.yobidashi4.domain.model.article

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import java.time.LocalDate
import jp.toastkid.yobidashi4.domain.service.article.ArticleTemplate
import jp.toastkid.yobidashi4.domain.service.article.OffDayFinderService
import jp.toastkid.yobidashi4.domain.service.article.OozumoTemplate
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
    fun noopIfReaderReturnsNull() {
        mockkConstructor(UserTemplateStreamReader::class)
        every { anyConstructed<UserTemplateStreamReader>().invoke() }.returns(null)

        val content = articleTemplate.invoke("test")
        assertTrue(content.isEmpty())
    }

    @Test
    fun testContainsWorkDay() {
        mockkConstructor(UserTemplateStreamReader::class)
        every { anyConstructed<UserTemplateStreamReader>().invoke() }.returns("""
            {{workday}}
test
            {{/workday}}
        """.trimIndent().byteInputStream())
        val content = ArticleTemplate(LocalDate.of(2023, 2, 23), offDayFinderService).invoke("test")
        assertTrue(content.contains("test"))
    }

    @Test
    fun testDoesNotContainsWorkDay() {
        mockkConstructor(UserTemplateStreamReader::class)
        every { anyConstructed<UserTemplateStreamReader>().invoke() }.returns("""
            {{workday}}
test
            {{/workday}}
        """.trimIndent().byteInputStream())
        val content = ArticleTemplate(LocalDate.of(2024, 1, 27), offDayFinderService).invoke("test")
        assertTrue(content.trim().isEmpty())
    }

    @Test
    fun testContainsBeltDay() {
        mockkConstructor(UserTemplateStreamReader::class)
        every { anyConstructed<UserTemplateStreamReader>().invoke() }.returns("""
            {{belt}}
test
            {{/belt}}
        """.trimIndent().byteInputStream())
        val content = ArticleTemplate(LocalDate.of(2023, 2, 23), offDayFinderService).invoke("test")
        assertTrue(content.contains("test"))
    }

    @Test
    fun testDoesNotContainsBeltDay() {
        mockkConstructor(UserTemplateStreamReader::class)
        every { anyConstructed<UserTemplateStreamReader>().invoke() }.returns("""
            {{belt}}
test
            {{/belt}}
        """.trimIndent().byteInputStream())
        val content = ArticleTemplate(LocalDate.of(2024, 1, 27), offDayFinderService).invoke("test")
        assertTrue(content.trim().isEmpty())
    }

    @Test
    fun testContainsMarketDay() {
        mockkConstructor(UserTemplateStreamReader::class)
        every { anyConstructed<UserTemplateStreamReader>().invoke() }.returns("""
            {{market}}
test
            {{/market}}
        """.trimIndent().byteInputStream())
        val content = ArticleTemplate(LocalDate.of(2023, 2, 22), offDayFinderService).invoke("test")
        assertTrue(content.contains("test"))
    }

    @Test
    fun testContainsMarketDayButOnSunday() {
        mockkConstructor(UserTemplateStreamReader::class)
        every { anyConstructed<UserTemplateStreamReader>().invoke() }.returns("""
            {{market}}
test
            {{/market}}
        """.trimIndent().byteInputStream())
        val content = ArticleTemplate(LocalDate.of(2023, 2, 19), offDayFinderService).invoke("test")
        assertTrue(content.trim().isEmpty())
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

    @Test
    fun testContainsStockDayButOnMonday() {
        mockkConstructor(UserTemplateStreamReader::class)
        every { anyConstructed<UserTemplateStreamReader>().invoke() }.returns("""
            {{stock}}
test
            {{/stock}}
        """.trimIndent().byteInputStream())
        val content = ArticleTemplate(LocalDate.of(2024, 1, 29), offDayFinderService).invoke("test")
        assertTrue(content.trimIndent().isEmpty())
    }

    @Test
    fun testContainsOozumoDay() {
        mockkConstructor(UserTemplateStreamReader::class, OozumoTemplate::class)
        every { anyConstructed<OozumoTemplate>().invoke(any()) }.returns("Oozumo")
        every { anyConstructed<UserTemplateStreamReader>().invoke() }.returns("""
            {{oozumo}}
        """.trimIndent().byteInputStream())
        val content = ArticleTemplate(LocalDate.of(2024, 1, 26), offDayFinderService).invoke("test")
        assertTrue(content.contains("Oozumo"))
    }

    @Test
    fun testDoesNotContainsOozumoDay() {
        mockkConstructor(UserTemplateStreamReader::class, OozumoTemplate::class)
        every { anyConstructed<OozumoTemplate>().invoke(any()) }.returns(null)
        every { anyConstructed<UserTemplateStreamReader>().invoke() }.returns("""
            {{oozumo}}
        """.trimIndent().byteInputStream())
        val content = ArticleTemplate(LocalDate.of(2024, 2, 26), offDayFinderService).invoke("test")
        assertTrue(content.trimIndent().isEmpty())
    }

    @Test
    fun testPlainText() {
        mockkConstructor(UserTemplateStreamReader::class)
        every { anyConstructed<UserTemplateStreamReader>().invoke() }.returns("""
test
        """.trimIndent().byteInputStream())
        val content = ArticleTemplate(LocalDate.of(2023, 2, 23), offDayFinderService).invoke("test")
        assertTrue(content.contains("test"))
    }

}