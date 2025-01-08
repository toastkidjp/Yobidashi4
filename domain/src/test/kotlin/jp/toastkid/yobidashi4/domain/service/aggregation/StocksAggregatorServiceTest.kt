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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.nameWithoutExtension

internal class StocksAggregatorServiceTest {

    @InjectMockKs
    private lateinit var stocksAggregatorService: StocksAggregatorService

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        val path = mockk<Path>()
        every { path.nameWithoutExtension } returns "file.md"

        mockkStatic(Files::class)
        val lines = """
_
評価額は12,202円、評価損益は6,838円(+11.12%)だった。
_
""".split("_").map { it.trim() }

        every { Files.readAllLines(any()) } returns lines

        MockKAnnotations.init(this)
        every { articlesReaderService.invoke() } returns Stream.of(path)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testInvoke() {
        val outgoAggregationResult = stocksAggregatorService.invoke("file")

        outgoAggregationResult.itemArrays().firstOrNull()?.let {
            Assertions.assertEquals("file", it[0])
            Assertions.assertEquals(12202, it[1])
            Assertions.assertEquals(6838, it[2])
            Assertions.assertEquals(11.12, it[3])
        }
        verify(exactly = 1) { Files.readAllLines(any()) }
        verify(exactly = 1) { articlesReaderService.invoke() }
    }

    @Test
    fun irregularInputCase() {
        val lines = """
_
評価額はxxx円、評価損益はyyyy円(+zz%)だった。
_
""".split("_").map { it.trim() }
        every { Files.readAllLines(any()) }.returns(lines)

        val outgoAggregationResult = stocksAggregatorService.invoke("file")

        outgoAggregationResult.itemArrays().firstOrNull()?.let {
            Assertions.assertEquals("file", it[0])
            Assertions.assertEquals(0, it[1])
            Assertions.assertEquals(0, it[2])
            Assertions.assertEquals(0.0, it[3])
        }
        verify(exactly = 1) { Files.readAllLines(any()) }
        verify(exactly = 1) { articlesReaderService.invoke() }
    }

}