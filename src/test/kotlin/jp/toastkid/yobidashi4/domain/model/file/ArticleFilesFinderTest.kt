package jp.toastkid.yobidashi4.domain.model.file

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.util.stream.Stream
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ArticleFilesFinderTest {

    @InjectMockKs
    private lateinit var subject: ArticleFilesFinder

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Files::class)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test() {
        val path = mockk<Path>().also {
            every { it.fileName.toString() } returns "2021-01-02"
        }

        val path2 = mockk<Path>()
        every { path2.fileName.toString() } returns "Test"
        val path3 = mockk<Path>()
        every { path3.fileName.toString() } returns "『2021-01-02』"

        every { Files.list(any()) } returns Stream.of(path, path2, path3)
        every { Files.getLastModifiedTime(any()) }.returns(FileTime.fromMillis(System.currentTimeMillis()))

        val paths = subject.invoke(mockk())

        Assertions.assertEquals(2, paths.size)
    }

}