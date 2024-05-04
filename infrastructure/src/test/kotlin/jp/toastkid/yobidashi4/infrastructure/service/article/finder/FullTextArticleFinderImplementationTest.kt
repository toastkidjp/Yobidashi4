package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.service.article.finder.FullTextArticleFinder
import kotlin.io.path.nameWithoutExtension
import org.apache.lucene.document.Document
import org.apache.lucene.search.TopDocs
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FullTextArticleFinderImplementationTest {

    private lateinit var subject: FullTextArticleFinder

    @MockK
    private lateinit var fullTextSearch: FullTextSearch

    @MockK
    private lateinit var document: Document

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkObject(FullTextSearch)
        every { FullTextSearch.make(any()) } returns fullTextSearch
        mockkStatic(Path::class, Files::class)
        val path = mockk<Path>()
        every { Path.of(any<String>()) } returns path
        mockkConstructor(KeywordSearchFilter::class)
        every { fullTextSearch.search(any()) } returns TopDocs(mockk(), arrayOf(mockk(), mockk()))
        every { fullTextSearch.getDocument(any()) } returns document
        every { document["name"] } returns "test.md"
        every { document["path"] } returns "/path/to/test"
        every { document["content"] } returns "This is test content."
        every { Files.readAllLines(any()) } returns listOf("a", "b", "c")
        every { anyConstructed<KeywordSearchFilter>().invoke(any()) } returns true
        every { path.nameWithoutExtension } returns "test"

        subject = FullTextArticleFinderImplementation()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val aggregationResult = subject.invoke("test")

        println(aggregationResult.itemArrays().size)
    }

    @Test
    fun notFoundCase() {
        every { fullTextSearch.search(any()) } returns null

        val aggregationResult = subject.invoke("test")

        assertTrue(aggregationResult.isEmpty())
    }

    @Test
    fun filterOutCase() {
        every { fullTextSearch.getDocument(any()) } returns null

        val aggregationResult = subject.invoke("test")

        assertTrue(aggregationResult.isEmpty())
    }

    @Test
    fun filterOutByNullTileCase() {
        every { document["name"] } returns null

        val aggregationResult = subject.invoke("test")

        assertTrue(aggregationResult.isEmpty())
    }

    @Test
    fun filterOutByNoneWordLinesCase() {
        every { anyConstructed<KeywordSearchFilter>().invoke(any()) } returns false

        val aggregationResult = subject.invoke("test")

        assertTrue(aggregationResult.isEmpty())
    }

}