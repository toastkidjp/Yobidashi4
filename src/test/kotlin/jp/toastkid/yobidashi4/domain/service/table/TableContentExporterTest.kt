package jp.toastkid.yobidashi4.domain.service.table

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.MovieMemoExtractorResult
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TableContentExporterTest {

    @InjectMockKs
    private lateinit var tableContentExporter: TableContentExporter

    @MockK
    private lateinit var folderPaths: Path

    private lateinit var aggregationResult: AggregationResult

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        aggregationResult = MovieMemoExtractorResult().also {
            it.add("2023-03-25(Sat)", "『Test is beautiful』(2022年、日本)")
            it.add("2023-03-26(Sun)", "『The 千秋楽』(2023年、日本)")
        }

        mockkStatic(Paths::class)
        every { Paths.get(any<String>()) } returns folderPaths
        every { folderPaths.resolve(any<String>()) } returns folderPaths

        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.createDirectories(any()) } returns mockk()
        every { Files.write(any(), any<Iterable<CharSequence>>()) } returns mockk()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        tableContentExporter.invoke(aggregationResult)

        verify { Files.exists(any()) }
        verify(inverse = true) { Files.createDirectories(any()) }
        verify { Files.write(any(), any<Iterable<CharSequence>>()) }
    }

    @Test
    fun invokeWithFolderCreationIfTargetFolderDoesNotExists() {
        every { Files.exists(any()) } returns false

        tableContentExporter.invoke(aggregationResult)

        verify { Files.exists(any()) }
        verify { Files.createDirectories(any()) }
        verify { Files.write(any(), any<Iterable<CharSequence>>()) }
    }

    @Test
    fun exportTo() {
        assertTrue(TableContentExporter.exportTo().startsWith("user/"))
    }

}