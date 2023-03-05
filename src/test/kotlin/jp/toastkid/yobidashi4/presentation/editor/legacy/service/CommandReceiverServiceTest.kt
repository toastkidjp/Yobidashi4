package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.presentation.editor.legacy.view.EditorAreaView
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

internal class CommandReceiverServiceTest {

    @InjectMockKs
    private lateinit var commandReceiverService: CommandReceiverService

    @MockK
    private lateinit var editorAreaView: EditorAreaView

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { viewModel } bind(MainViewModel::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
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