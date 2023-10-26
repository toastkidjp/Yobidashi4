package jp.toastkid.yobidashi4.presentation.main.menu

import androidx.compose.foundation.ExperimentalFoundationApi
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
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

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Test
    fun invoke() {
        val textContextMenu = textContextMenuFactory.invoke()
        assertNotNull(textContextMenu)
    }
}