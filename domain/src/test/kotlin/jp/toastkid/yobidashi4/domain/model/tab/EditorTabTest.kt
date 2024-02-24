package jp.toastkid.yobidashi4.domain.model.tab

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EditorTabTest {

    private lateinit var editorTab: EditorTab

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var fileName: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { path.fileName } returns fileName
        every { fileName.toString() } returns "test.md"
        mockkStatic(Files::class)
        every { Files.readString(any()) } returns "Test strings is good."

        editorTab = EditorTab(path)
        editorTab.setContent(editorTab.getContent(), false)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun title() {
        assertEquals("test", editorTab.title())
    }

    @Test
    fun titleWithEditingMark() {
        editorTab.setContent("test2", false)

        assertEquals("test * ", editorTab.title())
    }

    @Test
    fun closeable() {
        assertTrue(editorTab.closeable())
    }

    @Test
    fun test() {
        val countDownLatch = CountDownLatch(1)
        val job = CoroutineScope(Dispatchers.Unconfined).launch {
            editorTab.update().collect {
                countDownLatch.countDown()
            }
        }

        editorTab.setContent("test", false)

        countDownLatch.await(3, TimeUnit.SECONDS)
        assertEquals(0, countDownLatch.count)
        assertFalse(editorTab.closeable())
        job.cancel()
    }

    @Test
    fun switchPreview() {
        assertFalse(editorTab.showPreview())

        editorTab.switchPreview()

        assertTrue(editorTab.showPreview())

        editorTab.switchPreview()

        assertFalse(editorTab.showPreview())
    }

    @Test
    fun switchEditable() {
        assertTrue(editorTab.editable())

        editorTab.switchEditable()

        assertFalse(editorTab.editable())
    }

    @Test
    fun resetEditable() {
        editorTab.setContent("", true)
    }

    @Test
    fun attemptToSetNull() {
        editorTab.setContent(null, false)
    }

    @Test
    fun iconPath() {
        assertTrue(editorTab.iconPath().startsWith("images/icon/"))
    }

    @Test
    fun caretPosition() {
        assertEquals(0, editorTab.caretPosition())

        editorTab.setCaretPosition(1)

        assertEquals(1, editorTab.caretPosition())
    }

    @Test
    fun scroll() {
        assertEquals(0.0, editorTab.scroll())

        editorTab.setScroll(0.1)

        assertEquals(0.1, editorTab.scroll())
    }

    @Test
    fun loadContent() {
        editorTab.loadContent()

        verify { Files.readString(any()) }
        assertEquals("Test strings is good.", editorTab.getContent())
    }

    @Test
    fun checkGuard() {
        val job = CoroutineScope(Dispatchers.Unconfined).launch {
            editorTab.update().collect {
                fail("Never be called this line in this test case.")
            }
        }

        editorTab.setContent("test", false)
        editorTab.setContent("test", false)

        job.cancel()
    }

}