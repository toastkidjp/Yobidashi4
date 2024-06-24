package jp.toastkid.yobidashi4.domain.service.aggregation

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.model.aggregation.OutgoAggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import kotlin.io.path.nameWithoutExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EatingOutCounterServiceTest {

    private lateinit var aggregatorService: EatingOutCounterService

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        val path = mockk<Path>()
        every { path.nameWithoutExtension }.returns("2024-03-18.md")

        mockkStatic(Files::class)
        every { Files.readAllLines(any()) }.returns(
            """
## Text

## 家計簿
| 品目 | 金額 |
|:---|:---|
| (外食) マッシュルームとひき肉のカレー | 1000円
| 玉ねぎ8 | 218円
| にんじん | 129円
| いちごジャム | 118円
| Irregular Input | 18
| Empty Input | 円
| |
| (外食) マッシュルームとひき肉のスパゲッティ | 1100円
| ユニバースターゴールド | 268円
""".split("\n").map { it.trim() }
        )

        MockKAnnotations.init(this)
        every { articlesReaderService.invoke() }.returns(Stream.of(path))

        aggregatorService = EatingOutCounterService(articlesReaderService)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val result = aggregatorService.invoke("2024-03")

        verify(exactly = 1) { Files.readAllLines(any()) }
        verify(exactly = 1) { articlesReaderService.invoke() }
        assertEquals(2, result.itemArrays().size)
        if (result !is OutgoAggregationResult) {
            return fail()
        }
        assertEquals(2100, result.sum())
    }

    @Test
    fun monthlyCase() {
        val path = mockk<Path>()
        every { path.nameWithoutExtension }.returns("2024-02-23.md")

        val lines = """
_
## 家計簿_
| 品目 | 金額 |_
|:---|:---|_
| (外食) マッシュルームとひき肉のカレー | 1000円_
| 玉ねぎ8 | 218円_
"""
            .split("_").map { it.trim() }
        every { Files.readAllLines(any()) }.returns(lines)
        every { articlesReaderService.invoke() }.returns(Stream.of(path))

        val aggregationResult = aggregatorService.invoke("2024")

        if (aggregationResult !is OutgoAggregationResult) {
            return fail()
        }
        assertEquals(1000, aggregationResult.sum())
        assertEquals("2024-02", aggregationResult.itemArrays().first()[0])

        verify(exactly = 1) { Files.readAllLines(any()) }
        verify(exactly = 1) { articlesReaderService.invoke() }
    }
    
}