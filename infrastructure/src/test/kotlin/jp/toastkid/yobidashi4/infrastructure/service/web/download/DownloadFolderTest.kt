package jp.toastkid.yobidashi4.infrastructure.service.web.download

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DownloadFolderTest {

    private lateinit var subject: DownloadFolder

    @BeforeEach
    fun setUp() {
        mockkStatic(Files::class)

        subject = DownloadFolder()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun noopMakeIfNotNeed() {
        every { Files.exists(any()) } returns true
        every { Files.createDirectories(any()) } returns mockk()

        subject.makeIfNeed()

        verify { Files.exists(any()) }
        verify(inverse = true) { Files.createDirectories(any()) }
    }

    @Test
    fun makeIfNeed() {
        every { Files.exists(any()) } returns false
        every { Files.createDirectories(any()) } returns mockk()

        subject.makeIfNeed()

        verify { Files.exists(any()) }
        verify { Files.createDirectories(any()) }
    }

    @Test
    fun assignAbsolutePath() {
        assertNull(subject.assignAbsolutePath(null))
        val assignAbsolutePath = subject.assignAbsolutePath("test")
        assertTrue(assignAbsolutePath?.endsWith("test_000") ?: false)
        assertTrue(subject.assignAbsolutePath("test.jpg")?.endsWith("test_000.jpg") ?: false)
    }

    @Test
    fun assignQuickStorePath() {
        val assignQuickStorePath = subject.assignQuickStorePath("test.png")
        assertTrue(assignQuickStorePath.name.endsWith("_000.png"))
        assertTrue(subject.assignQuickStorePath("test.jpg").name.endsWith("_000.jpg"))
        assertTrue(subject.assignQuickStorePath("test").name.endsWith("_000.png"))

        every { Files.exists(any()) } answers {
            val path = this.args.get(0) as? Path ?: return@answers false
            return@answers path.name.endsWith("_000.png")
        }

        assertTrue(subject.assignQuickStorePath("test.png").name.endsWith("_001.png"))
        assertTrue(subject.assignQuickStorePath("https://test-img.jp/images/v3/FUTqMOrVt9rHy.jpg?errorImage=false").name.endsWith("_000.jpg"))
    }

}