package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.article.ArticleOpener
import jp.toastkid.yobidashi4.presentation.editor.legacy.MenuCommand
import jp.toastkid.yobidashi4.presentation.editor.legacy.view.EditorAreaView
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    @MockK
    private lateinit var articleOpener: ArticleOpener

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { viewModel.editorCommandFlow() }.returns(flowOf())
        mockkConstructor(ArticleOpener::class)
        coEvery { anyConstructed<ArticleOpener>().fromRawText(any()) } just Runs

        startKoin {
            modules(
                module {
                    single(qualifier=null) { viewModel } bind(MainViewModel::class)
                    single(qualifier=null) { setting } bind(Setting::class)
                    single(qualifier = null) { articleOpener } bind(ArticleOpener::class)
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
    fun testCodeBlock() {
        coEvery { viewModel.editorCommandFlow() }.returns(flowOf(MenuCommand.CODE_BLOCK))
        coEvery { editorAreaView.replaceSelected(any(), any()) }.just(Runs)

        CoroutineScope(Dispatchers.Unconfined).launch {
            commandReceiverService.invoke()
        }

        coVerify { editorAreaView.replaceSelected(any(), any()) }
    }

    @Test
    fun openArticle() {
        coEvery { viewModel.editorCommandFlow() }.returns(flowOf(MenuCommand.OPEN_ARTICLE))
        coEvery { editorAreaView.selectedText() }.returns("including [[Internal link]] text.")
        coEvery { articleOpener.fromRawText(any()) } just Runs

        runBlocking {
            commandReceiverService.invoke()

            coVerify { articleOpener.fromRawText(any()) }
        }
    }

}