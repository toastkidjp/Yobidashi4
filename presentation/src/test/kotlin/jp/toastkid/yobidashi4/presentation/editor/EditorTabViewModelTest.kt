package jp.toastkid.yobidashi4.presentation.editor

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EditorTabViewModelTest {

    private lateinit var subject: EditorTabViewModel

    @BeforeEach
    fun setUp() {
        subject = EditorTabViewModel()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun status() {
        assertTrue(subject.status().isEmpty())
    }

    @Test
    fun updateStatus() {
        subject.updateStatus("test")
        assertEquals("test", subject.status())
    }

    @Test
    fun showPreview() {
        assertFalse(subject.showPreview())
    }

    @Test
    fun preview() {
        assertEquals(0, subject.preview().lines().size)
    }

    @Test
    fun launchWithNotEditableTab() {
        val tab = spyk(EditorTab(mockk()))
        every { tab.update() } returns emptyFlow()

        CoroutineScope(Dispatchers.IO).launch {
            subject.launch(tab)
        }

        subject.updateStatus("123")
    }

    @Test
    fun launchWithEditableTab() {
        val tab = spyk(EditorTab(mockk()))
        every { tab.editable() } returns true

        CoroutineScope(Dispatchers.IO).launch {
            subject.launch(tab)
        }

        repeat(10) {
            subject.updateStatus("123")

            if (subject.status().startsWith("Not")) {
                Thread.sleep(500L)
            }
        }

        assertEquals("123", subject.status())
    }

}