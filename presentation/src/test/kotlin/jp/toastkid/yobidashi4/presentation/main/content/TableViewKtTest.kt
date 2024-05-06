package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.aggregation.FindResult
import jp.toastkid.yobidashi4.domain.model.aggregation.StepsAggregationResult
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.domain.model.tab.TableTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class TableViewKtTest {

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var articleFactory: ArticleFactory

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                    single(qualifier=null) { articleFactory } bind(ArticleFactory::class)
                }
            )
        }

        every { mainViewModel.updateScrollableTab(any(), any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tableView() {
        runDesktopComposeUiTest {
            setContent {
                val result = StepsAggregationResult()
                result.put("2022-12-22", 1200, 12)
                result.put("2022-12-23", 1240, 12)
                result.put("2022-12-24", 1230, 12)

                TableView(TableTab("test", result))
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun findResultCase() {
        runDesktopComposeUiTest {
            setContent {
                val result = FindResult("test")
                result.add("2022-12-22", listOf("1st", "2nd"))
                result.add("2022-12-22", listOf("1st", "2nd"))

                TableView(TableTab("test", result))
            }
        }
    }
}