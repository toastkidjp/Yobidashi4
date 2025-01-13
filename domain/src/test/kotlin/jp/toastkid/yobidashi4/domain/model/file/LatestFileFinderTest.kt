package jp.toastkid.yobidashi4.domain.model.file

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.stream.Stream

class LatestFileFinderTest {

    @InjectMockKs
    private lateinit var subject: LatestFileFinder

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
        val path = mockk<Path>()
        val path2 = mockk<Path>()
        val path3 = mockk<Path>()
        val now = LocalDateTime.now()
        every { Files.getLastModifiedTime(path) } returns FileTime.fromMillis(
            now.minusDays(8).toInstant(OffsetDateTime.now().offset).toEpochMilli())

        every { Files.getLastModifiedTime(path2) }.returns(FileTime.fromMillis(
            now.minusDays(6).toInstant(OffsetDateTime.now().offset).toEpochMilli())
        )
        every { Files.getLastModifiedTime(path3) }.returns(FileTime.fromMillis(
            now.minusDays(7).toInstant(OffsetDateTime.now().offset).toEpochMilli())
        )
        every { Files.list(any()) }.returns(Stream.of(path, path2, path3))

        val paths = subject.invoke(mockk(), now.minusDays(7))

        assertEquals(1, paths.size)
    }

}