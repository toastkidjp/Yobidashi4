package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.slideshow.Slide
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TextLine
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SlideViewTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(SlideViewModel::class)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun test() {
        val mutableSharedFlow = MutableSharedFlow<Float>(extraBufferCapacity = 1)
        every { anyConstructed<SlideViewModel>().scrollEventFlow() } returns mutableSharedFlow

        runDesktopComposeUiTest {
            setContent {
                SlideView(
                    Slide().also {
                        it.setFront(true)
                        it.setTitle("title")
                        it.addLine(TextLine("test"))
                        it.addLine(ImageLine("test"))
                        it.addLine(CodeBlockLine(""))
                        it.addLine(TableLine(listOf(), listOf()))
                        it.addLine(mockk())
                    },
                    { ImageBitmap(1, 1) }
                )
            }

            mutableSharedFlow.tryEmit(1f)
            waitForIdle()

            verify { anyConstructed<SlideViewModel>().scrollEventFlow() }
        }
    }
}