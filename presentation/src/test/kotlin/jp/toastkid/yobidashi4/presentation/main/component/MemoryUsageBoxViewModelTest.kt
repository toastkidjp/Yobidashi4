package jp.toastkid.yobidashi4.presentation.main.component

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.service.memory.MemoryUsage
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MemoryUsageBoxViewModelTest {

    private lateinit var subject: MemoryUsageBoxViewModel

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                }
            )
        }

        every { mainViewModel.switchMemoryUsageBox() } just Runs

        mockkConstructor(MemoryUsage::class)
        every { anyConstructed<MemoryUsage>().usedMemory() } returns "1"
        every { anyConstructed<MemoryUsage>().freeMemory() } returns "1"
        every { anyConstructed<MemoryUsage>().totalMemory() } returns "1"
        every { anyConstructed<MemoryUsage>().maxMemory() } returns "1"

        subject = MemoryUsageBoxViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun maxMemory() {
        assertEquals("Max memory: 1", subject.maxMemory())
    }

    @Test
    fun freeMemory() {
        assertEquals("Free memory: 1", subject.freeMemory())
    }

    @Test
    fun totalMemory() {
        assertEquals("Total memory: 1",subject.totalMemory())
    }

    @Test
    fun usedMemory() {
        assertEquals("Used memory: 1", subject.usedMemory())
    }

    @Test
    fun clickClose() {
        subject.clickClose()

        verify { mainViewModel.switchMemoryUsageBox() }
    }

    @Test
    fun launchGarbageCollection() {
        subject.launchGarbageCollection()
    }

}