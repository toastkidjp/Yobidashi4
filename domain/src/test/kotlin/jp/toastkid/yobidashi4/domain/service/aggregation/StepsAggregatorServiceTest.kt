package jp.toastkid.yobidashi4.domain.service.aggregation

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
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import kotlin.io.path.nameWithoutExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class StepsAggregatorServiceTest {

    @InjectMockKs
    private lateinit var stepsAggregatorService: StepsAggregatorService

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        val path = mockk<Path>()
        every { path.nameWithoutExtension }.returns("file.md")

        mockkStatic(Files::class)
        val lines = """
_
今日の歩数は4,122、消費カロリーは143kcalだった。
今日の歩数は追記、消費カロリーは追記kcalだった。
_
""".split("_").map { it.trim() }

        every { Files.readAllLines(any()) }.returns(lines)

        MockKAnnotations.init(this)
        every { articlesReaderService.invoke() }.returns(Stream.of(path))
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testInvoke() {
        val outgoAggregationResult = stepsAggregatorService.invoke("file")

        assertEquals(String::class.java, outgoAggregationResult.columnClass(0))
        assertEquals(Int::class.java, outgoAggregationResult.columnClass(1))
        assertEquals(Int::class.java, outgoAggregationResult.columnClass(2))

        outgoAggregationResult.itemArrays().firstOrNull { it[1] != 0 }?.let {
            assertTrue(outgoAggregationResult.title().isNotBlank())
            assertFalse(outgoAggregationResult.isEmpty())
            assertEquals(3, outgoAggregationResult.header().size)
            assertEquals("file", it[0])
            assertEquals(4122, it[1])
            assertEquals(143, it[2])
        }
        verify(exactly = 1) { Files.readAllLines(any()) }
        verify(exactly = 1) { articlesReaderService.invoke() }
    }

}