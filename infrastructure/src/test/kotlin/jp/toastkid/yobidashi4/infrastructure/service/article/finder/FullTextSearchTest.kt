package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.apache.lucene.index.CompositeReaderContext
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.StoredFields
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.store.ChecksumIndexInput
import org.apache.lucene.store.FSDirectory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FullTextSearchTest {

    private lateinit var subject: FullTextSearch

    @MockK
    private lateinit var indexSearcher: IndexSearcher

    @MockK
    private lateinit var indexReader: IndexReader

    @MockK
    private lateinit var storedFields: StoredFields

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { indexSearcher.search(any(), any<Int>()) } returns mockk()
        every { indexSearcher.indexReader } returns indexReader
        every { indexReader.storedFields() } returns storedFields
        every { storedFields.document(any()) } returns mockk()

        subject = FullTextSearch(indexSearcher)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun search() {
        subject.search("test")

        verify { indexSearcher.search(any(), any<Int>()) }
    }

    @Test
    fun searchWithQueryWhichStartQuestionMark() {
        subject.search("?test")

        verify { indexSearcher.search(any(), any<Int>()) }
    }

    @Test
    fun searchWithQueryWhichStartAsterisk() {
        subject.search("*test")

        verify { indexSearcher.search(any(), any<Int>()) }
    }

    @Test
    fun getDocument() {
        val scoreDoc = ScoreDoc(1, 1.0f)

        subject.getDocument(scoreDoc)

        verify { storedFields.document(any()) }
    }

    @Test
    fun make() {
        mockkStatic(FSDirectory::class, DirectoryReader::class)
        val fsDirectory = mockk<FSDirectory>()
        every { FSDirectory.open(any()) } returns fsDirectory
        every { fsDirectory.listAll() } returns arrayOf("segments_0")
        every { fsDirectory.obtainLock(any()) } returns mockk()
        every { fsDirectory.pendingDeletions } returns emptySet()
        val checksumIndexInput = mockk<ChecksumIndexInput>()
        every { fsDirectory.openChecksumInput(any(), any()) } returns checksumIndexInput
        every { checksumIndexInput.readByte() } returns 1
        val directoryReader = mockk<DirectoryReader>()
        every { DirectoryReader.open(any<FSDirectory>()) } returns directoryReader
        val compositeReaderContext = mockk<CompositeReaderContext>()
        every { directoryReader.context } returns compositeReaderContext
        val field = compositeReaderContext.javaClass.superclass.getDeclaredField("isTopLevel")
        field.isAccessible = true
        field.set(compositeReaderContext, true)
        every { compositeReaderContext.reader() } returns mockk()
        every { compositeReaderContext.leaves() } returns emptyList()

        val fullTextSearch = FullTextSearch.make(mockk())

        assertNotNull(fullTextSearch)
    }

}