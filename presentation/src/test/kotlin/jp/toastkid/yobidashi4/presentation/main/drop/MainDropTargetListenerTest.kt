package jp.toastkid.yobidashi4.presentation.main.drop

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import java.io.IOException
import java.nio.file.Path
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MainDropTargetListenerTest {

    @InjectMockKs
    private lateinit var subject: MainDropTargetListener

    @MockK
    private lateinit var event: DropTargetDropEvent

    @MockK
    private lateinit var transferable: Transferable

    @MockK
    private lateinit var consumer: (List<Path>) -> Unit

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkConstructor(DropTarget::class)
        every { anyConstructed<DropTarget>().addDropTargetListener(any()) }.just(Runs)

        every { event.acceptDrop(any()) } just Runs
        every { event.dropComplete(any()) } just Runs
        every { event.isDataFlavorSupported(any()) } returns true
        every { event.rejectDrop() } just Runs
        every { event.transferable } returns transferable
        val file = mockk<File>()
        every { transferable.getTransferData(any()) } returns listOf(file)
        val path = mockk<Path>()
        every { file.toPath() } returns path
        every { path.fileName.toString() } returns "test"
        every { consumer.invoke(any()) } just Runs

        subject = MainDropTargetListener(consumer)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        subject.drop(event)

        verify { consumer.invoke(any()) }
    }

    @Test
    fun unsupportedFlavorException() {
        every { event.isDataFlavorSupported(any()) } throws UnsupportedFlavorException(null)

        subject.drop(event)

        verify { consumer wasNot called }
        verify(inverse = true) { event.dropComplete(any()) }
        verify { event.rejectDrop() }
    }

    @Test
    fun ioException() {
        every { event.isDataFlavorSupported(any()) } throws IOException()

        subject.drop(event)

        verify { consumer wasNot called }
        verify(inverse = true) { event.dropComplete(any()) }
        verify { event.rejectDrop() }
    }

    @Test
    fun otherFlavorCase() {
        every { event.isDataFlavorSupported(any()) } returns false

        subject.drop(event)

        verify { consumer wasNot called }
        verify(inverse = true) { event.dropComplete(any()) }
        verify { event.rejectDrop() }
    }

    @Test
    fun passNull() {
        subject.drop(null)

        verify { consumer wasNot called }
        verify(inverse = true) { event.dropComplete(any()) }
        verify(inverse = true) { event.rejectDrop() }
    }

}