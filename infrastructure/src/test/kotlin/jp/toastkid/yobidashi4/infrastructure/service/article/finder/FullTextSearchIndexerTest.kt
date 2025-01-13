package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.store.FSDirectory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.util.stream.Stream
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.pathString

class FullTextSearchIndexerTest {

    private lateinit var subject: FullTextSearchIndexer

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Path::class, Files::class, FSDirectory::class, DirectoryReader::class)

        val fsDirectory = mockk<FSDirectory>()
        every { FSDirectory.open(any()) } returns fsDirectory
        every { fsDirectory.listAll() } returns arrayOf("test")
        every { fsDirectory.obtainLock(any()) } returns mockk()
        every { fsDirectory.pendingDeletions } returns emptySet()

        mockkConstructor(IndexWriter::class, IndexTargetFilter::class)
        every { anyConstructed<IndexWriter>().updateDocument(any(), any()) } returns 1
        every { anyConstructed<IndexWriter>().commit() } returns 1
        every { anyConstructed<IndexWriter>().numRamDocs() } returns 1
        every { anyConstructed<IndexTargetFilter>().invoke(any()) } returns true

        val path1 = mockk<Path>()
        every { path1.nameWithoutExtension } returns "test"
        every { path1.pathString } returns "test.md"

        every { Files.list(any()) } returns Stream.of(path1)
        every { Files.readString(any()) } returns "test"
        every { Files.getLastModifiedTime(any()) } returns FileTime.fromMillis(0)

        subject = FullTextSearchIndexer(path)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun createIndex() {
        subject.createIndex(mockk())

        verify { anyConstructed<IndexWriter>().updateDocument(any(), any()) }
    }

    @Test
    fun filterOut() {
        every { anyConstructed<IndexTargetFilter>().invoke(any()) } returns false

        subject.createIndex(mockk())

        verify(inverse = true) { anyConstructed<IndexWriter>().updateDocument(any(), any()) }
    }

    @Test
    fun close() {
        every { anyConstructed<IndexWriter>().close() } just Runs

        subject.close()

        verify { anyConstructed<IndexWriter>().close() }
    }

}