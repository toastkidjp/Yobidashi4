package jp.toastkid.yobidashi4.domain.service.aggregation

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.nameWithoutExtension

internal class Nikkei225AggregatorServiceTest {

    @InjectMockKs
    private lateinit var nikkei225AggregatorService: Nikkei225AggregatorService

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        val path = mockk<Path>()
        every { path.nameWithoutExtension } returns "file.md"

        mockkStatic(Files::class)
        val lines = """
_
## 今日の日経平均株価終値_
29,432.70円(48.18円高)_
_
""".split("_").map { it.trim() }

        every { Files.readAllLines(any()) } returns lines

        MockKAnnotations.init(this)
        every { articlesReaderService.invoke() }.returns(Stream.of(path))
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testInvoke() {
        val nikkei225AggregationResult = nikkei225AggregatorService.invoke("file")
        assertFalse(nikkei225AggregationResult.isEmpty())
        assertEquals(3, nikkei225AggregationResult.header().size)
        assertTrue(nikkei225AggregationResult.title().isNotEmpty())

        val first = nikkei225AggregationResult.itemArrays().first()
        assertEquals("29,432.70", first[1])
        assertEquals("48.18円高", first[2])

        verify(exactly = 1) { Files.readAllLines(any()) }
        verify(exactly = 1) { articlesReaderService.invoke() }
    }

}