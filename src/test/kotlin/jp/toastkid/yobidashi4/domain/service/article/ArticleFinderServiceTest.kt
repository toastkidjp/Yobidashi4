package jp.toastkid.yobidashi4.domain.service.article

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import javax.swing.JComponent
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField
import jp.toastkid.yobidashi4.domain.model.article.Article
import jp.toastkid.yobidashi4.domain.service.archive.ArticleFinderService
import jp.toastkid.yobidashi4.domain.service.archive.KeywordSearch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ArticleFinderServiceTest {

    private lateinit var articleFinderService: ArticleFinderService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        articleFinderService = ArticleFinderService()

        mockkConstructor(JPanel::class)
        every { anyConstructed<JPanel>().setLayout(any()) }.answers { mockk() }
        every { anyConstructed<JPanel>().add(any<JComponent>()) }.answers { mockk() }

        mockkConstructor(JTextField::class)
        every { anyConstructed<JTextField>().setPreferredSize(any()) }.answers { mockk() }

        mockkConstructor(KeywordSearch::class)
        coEvery { anyConstructed<KeywordSearch>().invoke(any(), any(), any()) }.returns(mutableListOf("test"))

        mockkObject(Article.Companion)
        coEvery { Article.withTitle(any()) }.returns(mockk())
    }

    @Test
    fun testNoneInputCase() {
        mockkStatic(JOptionPane::class)
        every { JOptionPane.showInputDialog(null, any()) }.answers { null }
        every { anyConstructed<JTextField>().getText() }.answers { null }

        articleFinderService.invoke()

        verify (exactly = 1) { anyConstructed<JPanel>().setLayout(any()) }
        verify (atLeast = 1) { anyConstructed<JPanel>().add(any<JComponent>()) }
        verify (exactly = 1) { anyConstructed<JTextField>().setPreferredSize(any()) }

        verify (exactly = 0) { anyConstructed<KeywordSearch>().invoke(any(), any(), any()) }
        verify (exactly = 0) { Article.withTitle(any()) }
    }

    @Test
    fun testNormalInputCase() = runBlocking {
        mockkStatic(JOptionPane::class)
        every { JOptionPane.showInputDialog(null, any()) }.answers { "test" }
        every { anyConstructed<JTextField>().getText() }.answers { "any" }

        articleFinderService.invoke()

        verify (exactly = 1) { anyConstructed<JPanel>().setLayout(any()) }
        verify (atLeast = 1) { anyConstructed<JPanel>().add(any<JComponent>()) }
        verify (exactly = 1) { anyConstructed<JTextField>().setPreferredSize(any()) }

        coVerify (exactly = 1) { Article.withTitle(any()) }
        coVerify (exactly = 1) { anyConstructed<KeywordSearch>().invoke(any(), any(), any()) }
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

}