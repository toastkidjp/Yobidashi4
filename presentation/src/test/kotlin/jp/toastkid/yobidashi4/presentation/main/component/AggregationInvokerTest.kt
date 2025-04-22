package jp.toastkid.yobidashi4.presentation.main.component

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.domain.service.aggregation.ArticleAggregator
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class AggregationInvokerTest {

    private lateinit var subject: AggregationInvoker

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var articleAggregator: ArticleAggregator

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }
        MockKAnnotations.init(this)
        every { mainViewModel.showSnackbar(any(), any(), any()) } just Runs

        subject = AggregationInvoker()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun invoke() {
        val aggregationResult = mockk<AggregationResult>()
        every { aggregationResult.isEmpty() } returns true
        every { articleAggregator.invoke(any()) } returns aggregationResult

        subject.invoke(articleAggregator, "test")

        verify { aggregationResult.isEmpty() }
        verify { articleAggregator.invoke(any()) }
        verify { mainViewModel.showSnackbar(any(), any(), any()) }
    }

    @Test
    fun invoke2() {
        val aggregationResult = mockk<AggregationResult>()
        every { aggregationResult.isEmpty() } returns false
        every { aggregationResult.title() } returns "test"
        every { articleAggregator.invoke(any()) } returns aggregationResult
        val capturingSlot = slot<Tab>()
        every { mainViewModel.openTab(capture(capturingSlot)) } just Runs
        every { mainViewModel.switchAggregationBox(any()) } just Runs

        subject.invoke(articleAggregator, "test")
        (capturingSlot.captured as TableTab).reload()

        verify(inverse = true) { mainViewModel.showSnackbar(any(), any(), any()) }
    }

}