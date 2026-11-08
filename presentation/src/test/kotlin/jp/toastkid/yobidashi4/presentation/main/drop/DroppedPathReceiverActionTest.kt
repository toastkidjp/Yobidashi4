package jp.toastkid.yobidashi4.presentation.main.drop

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path

class DroppedPathReceiverActionTest {

    @InjectMockKs
    private lateinit var subject: DroppedPathReceiverAction

    @MockK
    private lateinit var onReceive: (Path) -> Unit

    @MockK
    private lateinit var onComplete: (Collection<Path>) -> Unit

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { onReceive.invoke(any()) } just Runs
        every { onComplete.invoke(any()) } just Runs
    }

    @Test
    fun onReceive() {
        subject.onReceive(mockk())

        verify { onReceive.invoke(any()) }
    }

    @Test
    fun onComplete() {
        subject.onComplete(mockk())

        verify { onComplete.invoke(any()) }
    }

}