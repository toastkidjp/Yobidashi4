package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.ui.input.key.KeyEvent
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class AggregationBoxViewModelTest {

    private lateinit var subject: AggregationBoxViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                    single(qualifier = null) { articlesReaderService } bind (ArticlesReaderService::class)
                }
            )
        }
        every { mainViewModel.initialAggregationType() } returns 0
        every { mainViewModel.switchAggregationBox(any()) } just Runs
        every { mainViewModel.showAggregationBox() } returns true
        every { articlesReaderService.invoke() } returns Stream.empty()

        subject = AggregationBoxViewModel()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun focusingModifier() {
        subject.focusingModifier()
    }

    @Test
    fun onKeyEvent() {
        every { mainViewModel.switchAggregationBox(any()) } just Runs
        val consumed = subject.onKeyEvent(
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_PRESSED,
                    1,
                    java.awt.event.KeyEvent.CTRL_DOWN_MASK,
                    java.awt.event.KeyEvent.VK_ESCAPE,
                    'A'
                )
            )
        )

        assertTrue(consumed)
        every { mainViewModel.switchAggregationBox(any()) }
    }

    @Test
    fun notConsumedOnKeyEvent() {
        val consumed = subject.onKeyEvent(
            KeyEvent(
                java.awt.event.KeyEvent(
                    mockk(),
                    java.awt.event.KeyEvent.KEY_RELEASED,
                    1,
                    java.awt.event.KeyEvent.CTRL_DOWN_MASK,
                    java.awt.event.KeyEvent.VK_1,
                    'A'
                )
            )
        )

        assertFalse(consumed)
    }

    @Test
    fun switchAggregationBox() {
        every { mainViewModel.switchAggregationBox(any()) } just Runs

        subject.switchAggregationBox(true)

        verify { mainViewModel.switchAggregationBox(any()) }
    }

    @Test
    fun selectedCategoryName() {
        println(subject.selectedCategoryName())
    }

    @Test
    fun isNotCurrentSwingContent() {
        every { mainViewModel.currentTab() } returns mockk()

        assertFalse(subject.isCurrentSwingContent())
    }

    @Test
    fun isCurrentSwingContent() {
        every { mainViewModel.currentTab() } returns mockk<WebTab>()

        assertTrue(subject.isCurrentSwingContent())
    }

    @Test
    fun closeChooser() {
        assertFalse(subject.isOpeningChooser())

        subject.openChooser()

        assertTrue(subject.isOpeningChooser())

        subject.closeChooser()

        assertFalse(subject.isOpeningChooser())
    }

    @Test
    fun items() {
        assertFalse(subject.items().isEmpty())
    }

}