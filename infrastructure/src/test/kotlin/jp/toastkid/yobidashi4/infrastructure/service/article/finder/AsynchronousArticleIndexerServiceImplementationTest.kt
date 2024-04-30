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
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.article.finder.AsynchronousArticleIndexerService
import kotlinx.coroutines.Dispatchers
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.store.FSDirectory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class AsynchronousArticleIndexerServiceImplementationTest {

    private lateinit var subject: AsynchronousArticleIndexerService

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { setting.articleFolderPath() } returns mockk()
        startKoin {
            modules(
                module {
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }
        mockkStatic(Path::class, Files::class, FSDirectory::class, DirectoryReader::class)
        every { Path.of(any<String>()) } returns mockk()
        every { Files.exists(any()) } returns true

        // For mocking indexer
        val fsDirectory = mockk<FSDirectory>()
        every { FSDirectory.open(any()) } returns fsDirectory
        every { fsDirectory.listAll() } returns arrayOf("test")
        every { fsDirectory.obtainLock(any()) } returns mockk()
        every { fsDirectory.pendingDeletions } returns emptySet()
        mockkConstructor(IndexWriter::class)
        every { anyConstructed<IndexWriter>().addDocument(any()) } returns 1
        mockkConstructor(FullTextSearchIndexer::class)
        every { anyConstructed<FullTextSearchIndexer>().createIndex(any()) } returns 1
        every { anyConstructed<FullTextSearchIndexer>().close() } just Runs

        subject = AsynchronousArticleIndexerServiceImplementation()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun invoke() {
        subject.invoke(Dispatchers.Unconfined)

        verify { anyConstructed<FullTextSearchIndexer>().createIndex(any()) }
        verify { anyConstructed<FullTextSearchIndexer>().close() }
    }

    @Test
    fun clearPreviousIndex() {
        every { Files.exists(any()) } returns false

        subject.invoke(Dispatchers.Unconfined)

        verify(inverse = true) { anyConstructed<FullTextSearchIndexer>().createIndex(any()) }
        verify(inverse = true) { anyConstructed<FullTextSearchIndexer>().close() }
    }

    @Test
    fun forTestCoverage() {
        subject.invoke()
    }

}