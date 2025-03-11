package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import org.apache.lucene.index.CompositeReaderContext
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.search.IndexSearcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DocumentGetterAdapterTest {

    private lateinit var subject: DocumentGetterAdapter

    @MockK
    private lateinit var indexReader: DirectoryReader

    @MockK
    private lateinit var context: CompositeReaderContext

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        val field = context::class.java.superclass.getDeclaredField("isTopLevel")
        field.isAccessible = true
        field.set(context, true)
        every { context.reader() } returns indexReader
        every { context.leaves() } returns emptyList()
        every { indexReader.context } returns context
        every { indexReader.storedFields() } returns mockk()

        subject = DocumentGetterAdapter(IndexSearcher(indexReader))
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        Assertions.assertNotNull(subject.invoke())
    }

}