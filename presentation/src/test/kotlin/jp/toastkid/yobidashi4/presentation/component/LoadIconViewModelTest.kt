package jp.toastkid.yobidashi4.presentation.component

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoadIconViewModelTest {

    private lateinit var subject: LoadIconViewModel

    private val iconPath = "images/icon/ic_notification.xml"

    @BeforeEach
    fun setUp() {
        subject = LoadIconViewModel()

        mockkStatic(Files::class, Path::class)
        every { Files.exists(any()) } returns true
        every { Files.newInputStream(any()) } returns InputStream.nullInputStream()
        every { Path.of(any<String>()) } returns mockk()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun useIcon() {
        assertTrue(subject.useIcon(iconPath))
    }

    @Test
    fun useIconDoesNotExistsCase() {
        assertFalse(subject.useIcon(null))
    }

    @Test
    fun useIconPassingIncorrectCase() {
        assertFalse(subject.useIcon("test"))
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
    fun noopWithFileDoesNotExists() {
        every { Files.exists(any()) } returns false

        subject.loadBitmap(iconPath)

        verify { Files.exists(any()) }
        verify(inverse = true) { Files.newInputStream(any()) }
    }

    @Test
    fun noopWithNullArgs() {
        subject.loadBitmap(null)

        verify(inverse = true) { Files.exists(any()) }
        verify(inverse = true) { Files.newInputStream(any()) }
    }

    @Test
    fun loadBitmap() {
        subject.loadBitmap(iconPath)

        verify { Files.exists(any()) }
        verify { Files.newInputStream(any()) }
    }

}