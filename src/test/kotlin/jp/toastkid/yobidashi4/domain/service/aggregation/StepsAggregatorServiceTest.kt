package jp.toastkid.yobidashi4.domain.service.aggregation

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService

internal class StepsAggregatorServiceTest {

    @InjectMockKs
    private lateinit var stepsAggregatorService: StepsAggregatorService

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        val file = mockk<File>()
        every { file.name }.returns("file.md")

        val path = mockk<Path>()
        every { path.toFile() }.returns(file)

        mockkStatic(Files::class)
        val lines = """
_
今日の歩数は4,122、消費カロリーは143kcalだった。
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

        outgoAggregationResult.itemArrays().firstOrNull()?.let {
            assertEquals("file", it[0])
            assertEquals(4122, it[1])
            assertEquals(143, it[2])
        }
        verify(exactly = 1) { Files.readAllLines(any()) }
        verify(exactly = 1) { articlesReaderService.invoke() }
    }

}