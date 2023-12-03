package jp.toastkid.yobidashi4.presentation.main.menu

import androidx.compose.foundation.ContextMenuState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.TextContextMenu
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.text.AnnotatedString
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TextContextMenuFactoryTest {

    @InjectMockKs
    private lateinit var textContextMenuFactory: TextContextMenuFactory

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @OptIn(ExperimentalFoundationApi::class)
    @MockK
    private lateinit var textManager: TextContextMenu.TextManager

    @MockK
    private lateinit var state: ContextMenuState

    @OptIn(ExperimentalFoundationApi::class)
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { mainViewModel.setTextManager(any()) } just Runs
        every { mainViewModel.webSearch(any()) } just Runs
        every { mainViewModel.showSnackbar(any(), any(), any()) } just Runs
        every { textManager.selectedText } returns AnnotatedString("test")
        every { textManager.cut } returns mockk()
        every { textManager.copy } returns mockk()
        every { textManager.paste } returns mockk()
        every { textManager.selectAll } returns mockk()
        every { state.status } returns mockk()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalTestApi::class)
    @Test
    fun invoke() {
        val textContextMenu = textContextMenuFactory.invoke()
        assertNotNull(textContextMenu)

        runDesktopComposeUiTest {
            setContent {
                textContextMenu.Area(textManager, state) {

                }
            }
        }
    }

}