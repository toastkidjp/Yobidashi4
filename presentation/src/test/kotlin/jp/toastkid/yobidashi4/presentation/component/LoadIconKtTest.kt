package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoadIconKtTest {

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Path::class, Files::class)
        every { Path.of(any<String>()) } returns path
        every { Files.newInputStream(any()) } returns "".byteInputStream()

        mockkConstructor(LoadIconViewModel::class)
        every { anyConstructed<LoadIconViewModel>().useIcon() } returns false
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun pathNull() {
        runDesktopComposeUiTest {
            setContent {
                LoadIcon(null, Modifier)
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun useIcon() {
        every { anyConstructed<LoadIconViewModel>().useIcon() } returns true

        runDesktopComposeUiTest {
            setContent {
                LoadIcon("images/icon/ic_web.xml", Modifier)

                verify { anyConstructed<LoadIconViewModel>().useIcon() }
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun bitmapIsNotNull() {
        every { anyConstructed<LoadIconViewModel>().loadBitmap() } returns ImageBitmap(1, 1)

        runDesktopComposeUiTest {
            setContent {
                LoadIcon("images/icon/ic_web.xml", Modifier)

                verify { anyConstructed<LoadIconViewModel>().useIcon() }
                verify { anyConstructed<LoadIconViewModel>().loadBitmap() }
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun bitmapIsNull() {
        every { anyConstructed<LoadIconViewModel>().loadBitmap() } returns null
        every { anyConstructed<LoadIconViewModel>().defaultIconPath() } returns "images/icon/ic_web.xml"

        runDesktopComposeUiTest {
            setContent {
                LoadIcon("images/icon/ic_web.xml", Modifier)

                verify { anyConstructed<LoadIconViewModel>().useIcon() }
                verify { anyConstructed<LoadIconViewModel>().loadBitmap() }
                verify { anyConstructed<LoadIconViewModel>().defaultIconPath() }
            }
        }
    }

}