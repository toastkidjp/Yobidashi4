package jp.toastkid.yobidashi4.domain.service.number

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Path
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameFileProviderTest {

    @InjectMockKs
    private lateinit var gameFileProvider: GameFileProvider

    @MockK
    private lateinit var folder: Path

    @MockK
    private lateinit var file: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Path::class)
        every { Path.of("user/number/place/games") } returns folder

        mockkStatic(Files::class)
        every { Files.exists(folder) } returns true
        every { Files.exists(file) } returns true
        every { Files.createDirectories(folder) } returns folder
        every { Files.createFile(file) } returns file

        every { folder.resolve(any<String>()) } returns file
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test() {
        gameFileProvider.invoke()

        verify (exactly = 2) { Path.of("user/number/place/games") }
        verify (exactly = 1) { Files.exists(folder) }
        verify (exactly = 1) { Files.exists(file) }
        verify (inverse = true) { Files.createDirectories(any()) }
        verify (inverse = true) { Files.createFile(any()) }
        verify (exactly = 2) { folder.resolve(any<String>()) }
    }

}