package jp.toastkid.yobidashi4.presentation.main.drop

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draganddrop.DragAndDropEvent
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import java.io.IOException
import java.nio.file.Path
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class DropTargetTest {
    
    @InjectMockKs
    private lateinit var subject: DropTarget

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var dragAndDropEvent: DragAndDropEvent

    @MockK
    private lateinit var event: DropTargetDropEvent

    @MockK
    private lateinit var transferable: Transferable

    @OptIn(ExperimentalComposeUiApi::class)
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
        every { dragAndDropEvent.nativeEvent } returns event
        every { mainViewModel.emitDroppedPath(any()) } just Runs

        subject = DropTarget()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun invoke() {
        subject.onDrop(dragAndDropEvent)

        verify { mainViewModel.emitDroppedPath(any()) }
    }

    @Test
    fun unsupportedFlavorException() {
        every { event.isDataFlavorSupported(any()) } throws UnsupportedFlavorException(null)

        subject.onDrop(dragAndDropEvent)

        verify { mainViewModel wasNot called }
        verify(inverse = true) { event.dropComplete(any()) }
        verify { event.rejectDrop() }
    }

    @Test
    fun ioException() {
        every { event.isDataFlavorSupported(any()) } throws IOException()

        subject.onDrop(dragAndDropEvent)

        verify { mainViewModel wasNot called }
        verify(inverse = true) { event.dropComplete(any()) }
        verify { event.rejectDrop() }
    }

    @Test
    fun otherFlavorCase() {
        every { event.isDataFlavorSupported(any()) } returns false

        subject.onDrop(dragAndDropEvent)

        verify { mainViewModel wasNot called }
        verify(inverse = true) { event.dropComplete(any()) }
        verify { event.rejectDrop() }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun passNull() {
        every { dragAndDropEvent.nativeEvent } returns null

        subject.onDrop(dragAndDropEvent)

        verify { mainViewModel wasNot called }
        verify(inverse = true) { event.dropComplete(any()) }
        verify(inverse = true) { event.rejectDrop() }
    }

}