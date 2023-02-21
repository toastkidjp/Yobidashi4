package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import java.nio.file.Path
import jp.toastkid.yobidashi4.presentation.editor.legacy.MenuCommand
import jp.toastkid.yobidashi4.presentation.editor.legacy.model.Editing
import jp.toastkid.yobidashi4.presentation.editor.legacy.view.EditorAreaView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CommandReceiverServiceTest {

    @InjectMockKs
    private lateinit var commandReceiverService: CommandReceiverService

    @MockK
    private lateinit var channel: Channel<MenuCommand>

    @MockK
    private lateinit var editorAreaView: EditorAreaView

    @MockK
    private lateinit var currentArticle: () -> Path

    @MockK
    private lateinit var editing: Editing

    @MockK
    private lateinit var resetFrameTitle: () -> Unit

    @MockK
    private lateinit var switchFinder: () -> Unit

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        CoroutineScope(Dispatchers.Unconfined).launch {
            commandReceiverService.invoke()
        }
        Unit
    }

}