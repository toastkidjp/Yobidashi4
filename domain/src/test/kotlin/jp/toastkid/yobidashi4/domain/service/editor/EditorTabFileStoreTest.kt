package jp.toastkid.yobidashi4.domain.service.editor

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EditorTabFileStoreTest {

    private lateinit var subject: EditorTabFileStore

    @MockK
    private lateinit var tab: EditorTab

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        subject = EditorTabFileStore()

        every { tab.path } returns mockk()
        every { tab.closeable() } returns false
        every { tab.getContent() } returns "test"
        every { tab.setContent(any(), any()) } just Runs

        mockkStatic(Files::class)
        every { Files.write(any(), any<ByteArray>()) } returns mockk()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        subject.invoke(tab, Dispatchers.Unconfined)

        verify { tab.closeable() }
        verify { tab.getContent() }
        verify { Files.write(any(), any<ByteArray>()) }
    }

    @Test
    fun emptyContentCase() {
        every { tab.getContent() } returns ""

        subject.invoke(tab, Dispatchers.Unconfined)

        verify { tab.closeable() }
        verify { tab.getContent() }
        verify(inverse = true) { Files.write(any(), any<ByteArray>()) }
    }

    @Test
    fun closeableCase() {
        every { tab.closeable() } returns true

        subject.invoke(tab, Dispatchers.Unconfined)

        verify { tab.closeable() }
        verify(inverse = true) { tab.getContent() }
        verify(inverse = true) { Files.write(any(), any<ByteArray>()) }
    }

}