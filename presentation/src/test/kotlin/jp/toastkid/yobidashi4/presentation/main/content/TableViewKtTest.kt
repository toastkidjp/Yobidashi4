package jp.toastkid.yobidashi4.presentation.main.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
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

        mockkConstructor(TableViewModel::class)
        every { anyConstructed<TableViewModel>().sort(any(), any()) } just Runs
        every { anyConstructed<TableViewModel>().openMarkdownPreview(any()) } just Runs
        every { anyConstructed<TableViewModel>().edit(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tableView() {
        val result = StepsAggregationResult()
        result.put("2022-12-22", 1200, 12)
        result.put("2022-12-23", 1240, 12)
        result.put("2022-12-24", 1230, 12)

        runDesktopComposeUiTest {
            setContent {
                TableView(TableTab("test", result))
            }

            onNode(hasText("Steps"), useUnmergedTree = true).onParent().performMouseInput {
                enter()
                click()
                click()
                exit()
            }
            verify { anyConstructed<TableViewModel>().sort(any(), any()) }

            onNode(hasText("Steps"), useUnmergedTree = true).onParent().performMouseInput {
                click()
                click()
                click()
            }
            verify { anyConstructed<TableViewModel>().sort(any(), any()) }

            val previewButton = onAllNodesWithContentDescription("Open preview", useUnmergedTree = true).onFirst()
            previewButton.performClick()
            verify { anyConstructed<TableViewModel>().openMarkdownPreview(any()) }
            previewButton.onParent().performMouseInput {
                enter()
                exit()
            }

            onAllNodesWithContentDescription("Open file", useUnmergedTree = true).onFirst().performClick()
            verify { anyConstructed<TableViewModel>().edit(any()) }
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