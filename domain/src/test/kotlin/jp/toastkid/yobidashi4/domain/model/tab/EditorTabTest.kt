package jp.toastkid.yobidashi4.domain.model.tab

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
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

}