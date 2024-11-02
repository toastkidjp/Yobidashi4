package jp.toastkid.yobidashi4.presentation.component

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.icon
import jp.toastkid.yobidashi4.presentation.main.content.mapper.TabIconMapper
import kotlin.io.path.pathString
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoadIconViewModelTest {

    private lateinit var subject: LoadIconViewModel

    private val iconPath = "images/icon/ic_notification.xml"

    @BeforeEach
    fun setUp() {
        mockkConstructor(TabIconMapper::class)
        every { anyConstructed<TabIconMapper>().invoke(any()) } returns Res.drawable.icon
        mockkConstructor(WebIcon::class)
        every { anyConstructed<WebIcon>().makeFolderIfNeed() } just Runs
        every { anyConstructed<WebIcon>().find(any()) } returns mockk()

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
    fun iconPathIfNotFoundFaviconCase() {
        every { anyConstructed<WebIcon>().find(any()) } returns null

        assertNull(subject.loadBitmap("test"))
    }

    @Test
    fun iconPathIfNotExistsFileCase() {
        every { Files.exists(any()) } returns false

        assertNull(subject.loadBitmap("test"))
    }

    @Test
    fun loadBitmap() {
        val path = mockk<Path>()
        every { anyConstructed<WebIcon>().find(any()) } returns path
        every { Files.exists(any()) } returns true
        every { path.pathString } returns "path/to/favicon"

        subject.loadBitmap(iconPath)

        verify { Files.exists(any()) }
        verify { Files.newInputStream(any()) }
    }

    @Test
    fun loadBitmapWithExceptionCase() {
        val path = mockk<Path>()
        every { anyConstructed<WebIcon>().find(any()) } returns path
        every { Files.exists(any()) } returns true
        every { path.pathString } returns "path/to/favicon"
        val inputStream = mockk<InputStream>()
        every { Files.newInputStream(any()) } returns inputStream
        every { inputStream.readAllBytes() } throws IllegalArgumentException()
        every { inputStream.close() } just Runs

        val bitmap = subject.loadBitmap(iconPath)

        assertNull(bitmap)
        verify { Files.exists(any()) }
        verify { Files.newInputStream(any()) }
        verify { inputStream.readAllBytes() }
        verify { inputStream.close() }
    }

    @Test
    fun loadTabIcon() {
        val tab = mockk<Tab>()
        subject.loadTabIcon(tab)

        verify { anyConstructed<TabIconMapper>().invoke(tab) }
    }

}