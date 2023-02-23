package jp.toastkid.yobidashi4.domain.model.article

import io.mockk.every
import io.mockk.mockkConstructor
import java.time.LocalDate
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.setting.TestSettingImplementation
import jp.toastkid.yobidashi4.domain.service.article.ArticleTemplate
import jp.toastkid.yobidashi4.domain.service.article.UserTemplateStreamReader
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

internal class ArticleTemplateTest {

    private lateinit var articleTemplate: ArticleTemplate

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier=null) { TestSettingImplementation() } bind(Setting::class)
                }
            )
        }
        articleTemplate = ArticleTemplate()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
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

}