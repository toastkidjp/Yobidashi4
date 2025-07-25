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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

internal class MovieMemoSubtitleExtractorTest {

    @InjectMockKs
    private lateinit var movieMemoSubtitleExtractor: MovieMemoSubtitleExtractor

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        val path = mockk<Path>()
        val fileName = mockk<Path>()
        every { path.getFileName() } returns fileName
        every { fileName.toString() }.returns("file.md")

        mockkStatic(Files::class)
        every { Files.readAllLines(any()) }
            .returns(
                listOf(
                    "## 『ミッション：インポッシブル(原題：MISSION:IMPOSSIBLE)』(1996年、アメリカ合衆国)",
                    "## 『JSA』(2001、韓国)",
                    "## 『WBS』",
                    "年、",
                )
            )

        MockKAnnotations.init(this)
        every { articlesReaderService.invoke() }.returns(Stream.of(path))
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testInvoke() {
        val movieMemoExtractorResult = movieMemoSubtitleExtractor.invoke("file")
        assertEquals(2, movieMemoExtractorResult.header().size)
        assertFalse(movieMemoExtractorResult.isEmpty())

        val first = movieMemoExtractorResult.itemArrays().first()
        assertEquals("file", first[0])
        assertEquals("『ミッション：インポッシブル(原題：MISSION:IMPOSSIBLE)』(1996年、アメリカ合衆国)", first[1])

        verify(exactly = 1) { Files.readAllLines(any()) }
        verify(exactly = 1) { articlesReaderService.invoke() }
    }

}