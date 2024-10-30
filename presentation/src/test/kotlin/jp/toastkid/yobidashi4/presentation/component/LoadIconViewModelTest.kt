package jp.toastkid.yobidashi4.presentation.component

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.icon
import jp.toastkid.yobidashi4.presentation.main.content.mapper.TabIconMapper
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
        mockkConstructor(TabIconMapper::class)
        every { anyConstructed<TabIconMapper>().invoke(any()) } returns Res.drawable.icon

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
        assertNotNull(subject.defaultIconPath())
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

    @Test
    fun loadTabIcon() {
        val tab = mockk<Tab>()
        subject.loadTabIcon(tab)

        verify { anyConstructed<TabIconMapper>().invoke(tab) }
    }

}