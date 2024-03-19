package jp.toastkid.yobidashi4.presentation.lib.input

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.repository.input.InputHistoryRepository
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class InputHistoryServiceTest {

    private lateinit var subject: InputHistoryService

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var inputHistoryRepository: InputHistoryRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { viewModel } bind (MainViewModel::class)
                    single(qualifier = null) { inputHistoryRepository } bind(InputHistoryRepository::class)
                }
            )
        }
        every { viewModel.initialAggregationType() } returns 0
        every { viewModel.switchAggregationBox(any()) } just Runs
        every { viewModel.showAggregationBox() } returns true
        every { inputHistoryRepository.add(any()) } just Runs
        every { inputHistoryRepository.list() } returns emptyList()
        every { inputHistoryRepository.filter(any()) } returns emptyList()
        every { inputHistoryRepository.deleteWithWord(any()) } just Runs
        every { inputHistoryRepository.clear() } just Runs

        subject = InputHistoryService("test")
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun filter() {
        val items = mockk<MutableList<InputHistory>>()
        every { items.clear() } just Runs
        every { items.addAll(any()) } returns true

        subject.filter(items, "test")

        verify { items.clear() }
        verify { items.addAll(any()) }
        verify { inputHistoryRepository.filter(any()) }
    }

    @Test
    fun noopAddWithNull() {
        subject.add(null)

        verify { inputHistoryRepository wasNot called }
    }

    @Test
    fun add() {
        subject.add("test")

        verify { inputHistoryRepository.add(any()) }
    }

    @Test
    fun addWithNull() {
        subject.add(null)

        verify { inputHistoryRepository wasNot called }
    }

    @Test
    fun make() {
        val textFieldValue = subject.make("test")

        assertEquals("test ", textFieldValue?.text)
    }

    @Test
    fun makeWithNull() {
        val textFieldValue = subject.make(null)

        assertNull(textFieldValue)
    }

    @Test
    fun inputHistories() {
        val mutableList = mutableListOf<InputHistory>()

        subject.inputHistories(mutableList)
    }

    @Test
    fun shouldShowInputHistory() {
        assertTrue(subject.shouldShowInputHistory(listOf(mockk())))
        assertFalse(subject.shouldShowInputHistory(emptyList()))
    }

    @Test
    fun delete() {
        subject.delete(mutableListOf(), "test")

        verify { inputHistoryRepository.deleteWithWord(any()) }
    }

    @Test
    fun clear() {
        val slot = slot<() -> Unit>()
        every { viewModel.showSnackbar(any(), any(), capture(slot)) } just Runs

        subject.clear(mutableListOf())
        slot.captured.invoke()

        verify { inputHistoryRepository.list() }
        verify { inputHistoryRepository.clear() }
    }

}