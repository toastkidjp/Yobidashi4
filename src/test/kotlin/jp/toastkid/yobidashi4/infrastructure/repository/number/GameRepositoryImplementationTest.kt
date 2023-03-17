package jp.toastkid.yobidashi4.infrastructure.repository.number

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameRepositoryImplementationTest {

    @InjectMockKs
    private lateinit var gameRepositoryImplementation: GameRepositoryImplementation

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Files::class)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun save() {
    }

    @Test
    fun delete() {
        every { Files.delete(any()) }.just(Runs)

        gameRepositoryImplementation.delete(mockk())

        verify { Files.delete(any()) }
    }
}