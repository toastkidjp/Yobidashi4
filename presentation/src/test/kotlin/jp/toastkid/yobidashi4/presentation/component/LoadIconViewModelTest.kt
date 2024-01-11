package jp.toastkid.yobidashi4.presentation.component

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.io.InputStream
import java.nio.file.Files
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoadIconViewModelTest {

    private lateinit var subject: LoadIconViewModel

    @BeforeEach
    fun setUp() {
        subject = LoadIconViewModel("images/icon/ic_notification.xml")

        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.newInputStream(any()) } returns InputStream.nullInputStream()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun useIcon() {
        assertTrue(subject.useIcon())
    }

    @Test
    fun useIconDoesNotExistsCase() {
        every { Files.exists(any()) } returns false

        assertFalse(subject.useIcon())
    }

    @Test
    fun defaultIconPath() {
        assertTrue(subject.defaultIconPath().startsWith("images/icon"))
    }

    @Test
    fun contentDescription() {
        assertNotNull(subject.contentDescription())
    }

    @Test
    fun noopWithEmptyPath() {
        subject = LoadIconViewModel("")

        subject.loadBitmap()

        verify(inverse = true) { Files.exists(any()) }
    }

    @Test
    fun noopWithFileDoesNotExists() {
        every { Files.exists(any()) } returns false

        subject.loadBitmap()

        verify { Files.exists(any()) }
        verify(inverse = true) { Files.newInputStream(any()) }
    }

    @Test
    fun loadBitmap() {
        subject.loadBitmap()

        verify { Files.exists(any()) }
        verify { Files.newInputStream(any()) }
    }

}