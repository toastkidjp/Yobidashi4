package jp.toastkid.yobidashi4.domain.service.article

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.service.archive.KeywordSearch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class KeywordSearchTest {

    private lateinit var keywordSearch: KeywordSearch

    @MockK
    private lateinit var path1: Path

    @MockK
    private lateinit var path2: Path

    @BeforeEach
    fun setUp() {
        keywordSearch = KeywordSearch()

        MockKAnnotations.init(this)

        mockkStatic(Files::class)
        every { Files.readAllLines(any()) }.returns(listOf("tomato test"))

        val mockFile1 = mockk<File>()
        every { mockFile1.getName() }.returns("mockFile1")
        every { path1.toFile() }.returns(mockFile1)

        val mockFile2 = mockk<File>()
        every { mockFile2.getName() }.returns("mockFile2")
        every { path2.toFile() }.returns(mockFile2)
    }

    @Test
    fun test() {
        val result = keywordSearch.invoke("test", "mock", Stream.of(path1, path2))

        assertEquals(2, result.size)
    }

    @Test
    fun testUseTitleFilter() {
        val result = keywordSearch.invoke("test", "1", Stream.of(path1, path2))

        assertEquals(1, result.size)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

}