package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.SearcherManager
import org.apache.lucene.search.TopDocs
import org.apache.lucene.search.TotalHits
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FullTextSearchTest {

    private lateinit var searcherManager: SearcherManager

    private lateinit var indexSearcher: IndexSearcher

    private lateinit var fullTextSearch: FullTextSearch

    @BeforeEach
    fun setUp() {
        searcherManager = mockk(relaxed = true)
        indexSearcher = mockk(relaxed = true)

        every { searcherManager.acquire() } returns indexSearcher

        fullTextSearch = FullTextSearch(searcherManager)
    }

    @Test
    fun testSearchShouldReturnTopDocsWhenMatchExists() {
        val searchQueryInput = "Kotlin"
        val expectedTopDocs = TopDocs(
            TotalHits(1, TotalHits.Relation.EQUAL_TO),
            arrayOf(ScoreDoc(0, 1.0f))
        )

        every { indexSearcher.search(any<Query>(), 300) } returns expectedTopDocs

        val result = fullTextSearch.search(searchQueryInput)

        assertNotNull(result)
        assertEquals(1, result?.totalHits?.value)

        verify(exactly = 1) { searcherManager.maybeRefresh() }
        verify(exactly = 1) { searcherManager.acquire() }
        verify(exactly = 1) { searcherManager.release(indexSearcher) }
    }

    @Test
    fun testGetDocumentShouldReturnDocumentForGivenScoreDoc() {
        val scoreDoc = ScoreDoc(0, 1.0f)
        val expectedDocument = Document().apply {
            add(StringField("name", "doc1", Field.Store.YES))
            add(TextField("content", "Kotlin content", Field.Store.YES))
        }

        every { indexSearcher.storedFields().document(scoreDoc.doc) } returns expectedDocument

        val document = fullTextSearch.getDocument(scoreDoc)

        assertNotNull(document)
        assertEquals("doc1", document?.get("name"))

        verify(exactly = 1) { searcherManager.acquire() }
        verify(exactly = 1) { searcherManager.release(indexSearcher) }
    }

    @Test
    fun testCloseShouldCloseSearcherManager() {
        fullTextSearch.close()

        verify(exactly = 1) { searcherManager.close() }
    }

}