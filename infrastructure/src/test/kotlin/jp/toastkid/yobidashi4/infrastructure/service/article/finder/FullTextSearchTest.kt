package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.CompositeReaderContext
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.SearcherManager
import org.apache.lucene.search.TopDocs
import org.apache.lucene.search.TotalHits
import org.apache.lucene.store.ChecksumIndexInput
import org.apache.lucene.store.FSDirectory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

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

    @ParameterizedTest
    @CsvSource(
        "Kotlin",
        "?Kotlin",
        "*Kotlin"
    )
    fun testSearchShouldReturnTopDocsWhenMatchExists(searchQueryInput: String) {
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

    @Test
    fun testMake() {
        mockkStatic(FSDirectory::class, DirectoryReader::class)
        val fsDirectory = mockk<FSDirectory>()
        every { FSDirectory.open(any()) } returns fsDirectory
        every { fsDirectory.listAll() } returns arrayOf("segments_0")
        every { fsDirectory.obtainLock(any()) } returns mockk()
        every { fsDirectory.pendingDeletions } returns emptySet()
        val checksumIndexInput = mockk<ChecksumIndexInput>()
        every { fsDirectory.openChecksumInput(any()) } returns checksumIndexInput
        every { checksumIndexInput.readByte() } returns 1
        val directoryReader = mockk<DirectoryReader>()
        every { DirectoryReader.open(any<FSDirectory>()) } returns directoryReader
        val compositeReaderContext = mockk<CompositeReaderContext>()
        every { compositeReaderContext.reader() } returns directoryReader
        every { compositeReaderContext.leaves() } returns emptyList()
        every { directoryReader.context } returns compositeReaderContext
        every { directoryReader.decRef() } just Runs
        val field = compositeReaderContext.javaClass.superclass.getDeclaredField("isTopLevel")
        field.isAccessible = true
        field.set(compositeReaderContext, true)

        FullTextSearch.make(mockk())

        verify { FSDirectory.open(any()) }
    }

}