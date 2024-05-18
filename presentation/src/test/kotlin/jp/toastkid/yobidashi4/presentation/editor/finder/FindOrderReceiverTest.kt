package jp.toastkid.yobidashi4.presentation.editor.finder

import androidx.compose.ui.text.input.TextFieldValue
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.find.FindOrder
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class FindOrderReceiverTest {

    private lateinit var subject: FindOrderReceiver

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }

        subject = FindOrderReceiver()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun invoke() {
        every { mainViewModel.setFindStatus(any()) } just Runs
        every { mainViewModel.finderFlow() } returns flowOf(
            FindOrder.EMPTY,
            FindOrder("test", ""),
            FindOrder("test", "", true, true, false)
        )
        val setNewContent: (TextFieldValue) -> Unit = mockk()
        every { setNewContent.invoke(any()) } just Runs

        subject.invoke(FindOrder.EMPTY, TextFieldValue("test"), setNewContent)
        verify { setNewContent wasNot called }

        subject.invoke(FindOrder("test", ""), TextFieldValue("test"), setNewContent)
        verify { setNewContent.invoke(any()) }

        subject.invoke(FindOrder("test", "", true, true, false), TextFieldValue("test"), setNewContent)
        verify { setNewContent.invoke(any()) }
        verify { mainViewModel.setFindStatus(any()) }

        subject.invoke(FindOrder("", "", true, false, false), TextFieldValue("test"), setNewContent)
        verify { setNewContent.invoke(any()) }

        subject.invoke(FindOrder("at", ""), TextFieldValue("test"), setNewContent)
        verify { setNewContent.invoke(any()) }
    }

}