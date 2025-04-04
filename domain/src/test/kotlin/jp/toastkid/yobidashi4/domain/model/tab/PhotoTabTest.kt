package jp.toastkid.yobidashi4.domain.model.tab

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PhotoTabTest {

    private lateinit var subject: PhotoTab

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { path.absolutePathString() } returns "/path/to/test.png"
        every { path.fileName } returns path
        every { path.toString() } returns "test.png"

        subject = PhotoTab(path)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun title() {
        assertEquals("test.png", subject.title())
    }

    @Test
    fun path() {
        assertSame(path, subject.path())
    }

    @Test
    fun iconPath() {
        assertNull(subject.iconPath())
    }

}