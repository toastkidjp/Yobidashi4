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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ArticleLengthAggregatorServiceTest {

    @InjectMockKs
    private lateinit var articleLengthAggregatorService: ArticleLengthAggregatorService

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        val path = mockk<Path>()
        every { path.nameWithoutExtension }.returns("file.md")

        mockkStatic(Files::class)
        every { Files.readAllBytes(any()) }.returns("test content".toByteArray())

        MockKAnnotations.init(this)
        every { articlesReaderService.invoke() }.returns(Stream.of(path))
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        articleLengthAggregatorService.invoke("file")

        verify(exactly = 1) { Files.readAllBytes(any()) }
        verify(exactly = 1) { articlesReaderService.invoke() }
    }

}