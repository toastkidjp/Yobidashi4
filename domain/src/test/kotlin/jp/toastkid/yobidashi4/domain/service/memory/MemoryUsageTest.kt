package jp.toastkid.yobidashi4.domain.service.memory

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MemoryUsageTest {

    private lateinit var memoryUsage: MemoryUsage

    @MockK
    private lateinit var runtime: Runtime

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        memoryUsage = MemoryUsage(runtime)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun maxMemory() {
        assertTrue(memoryUsage.maxMemory().endsWith("[MB]"))
    }

    @Test
    fun freeMemory() {
        assertTrue(memoryUsage.freeMemory().endsWith("[MB]"))
    }

    @Test
    fun totalMemory() {
        assertTrue(memoryUsage.totalMemory().endsWith("[MB]"))
    }

    @Test
    fun usedMemory() {
        assertTrue(memoryUsage.usedMemory().endsWith("[MB]"))
    }
}