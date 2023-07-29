package jp.toastkid.yobidashi4.presentation.main.drop

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.dnd.DropTarget
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DropTargetFactoryTest {

    @InjectMockKs
    private lateinit var dropTargetFactory: DropTargetFactory

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkConstructor(DropTarget::class)
        every { anyConstructed<DropTarget>().addDropTargetListener(any()) }.just(Runs)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        dropTargetFactory.invoke {  }

        verify { anyConstructed<DropTarget>().addDropTargetListener(any()) }
    }
}