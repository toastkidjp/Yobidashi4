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
import jp.toastkid.yobidashi4.domain.model.number.NumberPlaceGame
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
        every { Files.write(any(), any<ByteArray>()) }.returns(mockk())

        gameRepositoryImplementation.save(mockk(), NumberPlaceGame())

        verify { Files.write(any(), any<ByteArray>()) }
    }

    @Test
    fun load() {
        every { Files.exists(any()) }.returns(true)
        every { Files.readString(any()) }.returns("""
{"correct":{"rows":[[1,7,9,6,8,2,3,4,5],[5,2,3,9,4,7,1,6,8],[8,6,4,3,5,1,9,7,2],[9,3,5,4,6,8,2,1,7],
[7,1,6,2,3,5,8,9,4],[4,8,2,7,1,9,6,5,3],[3,9,1,5,2,4,7,8,6],[6,5,8,1,7,3,4,2,9],[2,4,7,8,9,6,5,3,1]]},
"masked":{"rows":[[-1,7,9,6,8,2,-1,4,5],[5,2,3,9,4,7,1,6,8],[-1,6,4,3,5,1,9,7,2],[9,3,5,4,6,-1,-1,1,-1],
[7,1,-1,2,-1,-1,8,9,-1],[4,8,-1,7,-1,9,6,-1,-1],[3,9,-1,-1,2,4,7,8,6],[6,5,8,1,7,3,4,-1,9],[-1,4,-1,-1,9,6,5,3,1]]},
"solving":{"rows":[[-1,7,9,6,8,2,-1,4,5],[5,2,3,9,4,7,1,6,8],[-1,6,4,3,5,1,9,7,2],[9,3,5,4,6,-1,-1,1,-1],
[7,1,-1,2,-1,-1,8,9,-1],[4,8,-1,7,-1,9,6,-1,-1],[3,9,-1,-1,2,4,7,8,6],[6,5,8,1,7,3,4,-1,9],[-1,4,-1,-1,9,6,5,3,1]]}}
        """.replace("\n", ""))

        val loaded = gameRepositoryImplementation.load(mockk())

        assertNotNull(loaded)
    }

    @Test
    fun loadWithBlankContent() {
        every { Files.exists(any()) }.returns(true)
        every { Files.readString(any()) }.returns("")

        val loaded = gameRepositoryImplementation.load(mockk())

        assertNull(loaded)
    }

    @Test
    fun loadFileNotExistsCase() {
        every { Files.exists(any()) }.returns(false)

        val loaded = gameRepositoryImplementation.load(mockk())

        assertNull(loaded)
    }

    @Test
    fun delete() {
        every { Files.delete(any()) }.just(Runs)

        gameRepositoryImplementation.delete(mockk())

        verify { Files.delete(any()) }
    }
}