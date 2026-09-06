package jp.toastkid.yobidashi4.infrastructure.service.clustering

import jp.toastkid.yobidashi4.domain.service.tool.clustering.ClusteringDocumentReader
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ClusteringDocumentReaderImplementationTest {

    private lateinit var subject: ClusteringDocumentReader

    private val fileSystem = FakeFileSystem()

    @BeforeEach
    fun setUp() {
        subject = ClusteringDocumentReaderImplementation(fileSystem)
    }

    @Test
    fun invoke() {
        val path = "test".toPath()
        fileSystem.write(path) {}

        val pairs = subject.invoke(listOf(path.toNioPath()))

        assertEquals(1, pairs.size)
    }

}