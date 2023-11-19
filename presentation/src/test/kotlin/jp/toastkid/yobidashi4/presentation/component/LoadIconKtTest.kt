package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
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

}