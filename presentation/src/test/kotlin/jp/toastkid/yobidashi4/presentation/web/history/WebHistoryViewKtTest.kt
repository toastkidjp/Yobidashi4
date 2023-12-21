package jp.toastkid.yobidashi4.presentation.web.history

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.web.history.WebHistory
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.repository.web.history.WebHistoryRepository
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class WebHistoryViewKtTest {

    @MockK
    private lateinit var repository: WebHistoryRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier = null) { mockk<MainViewModel>() } bind (MainViewModel::class)
                    single(qualifier = null) { repository } bind (WebHistoryRepository::class)
                }
            )
        }
        every { repository.readAll() } returns listOf(WebHistory("test", "https://www.yahoo.co.jp"))
        mockkConstructor(WebIcon::class)
        every { anyConstructed<WebIcon>().readAll() } returns emptyList()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun webHistoryView() {
        runDesktopComposeUiTest {
            setContent {
                WebHistoryView()
            }
        }
    }
}