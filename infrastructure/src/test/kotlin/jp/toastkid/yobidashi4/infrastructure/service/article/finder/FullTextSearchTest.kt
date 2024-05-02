package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.apache.lucene.index.IndexReader
import org.apache.lucene.index.StoredFields
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.ScoreDoc
import org.junit.jupiter.api.AfterEach
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
    fun getDocument() {
        val scoreDoc = ScoreDoc(1, 1.0f)

        subject.getDocument(scoreDoc)

        verify { storedFields.document(any()) }
    }

}