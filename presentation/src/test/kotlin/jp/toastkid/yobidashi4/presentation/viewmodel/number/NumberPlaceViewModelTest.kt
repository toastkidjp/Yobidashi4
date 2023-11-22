package jp.toastkid.yobidashi4.presentation.viewmodel.number

import androidx.compose.runtime.mutableStateOf
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.repository.number.GameRepository
import jp.toastkid.yobidashi4.domain.service.number.GameFileProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class NumberPlaceViewModelTest {

    private lateinit var numberPlaceViewModel: NumberPlaceViewModel

    @MockK
    private lateinit var repository: GameRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { repository } bind(GameRepository::class)
                }
            )
        }

        every { repository.save(any(), any()) } just Runs
        every { repository.delete(any()) } just Runs

        numberPlaceViewModel = NumberPlaceViewModel()
        numberPlaceViewModel.initialize(20)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
        stopKoin()
    }

    @Test
    fun initializeSolving() {
        numberPlaceViewModel.initializeSolving()
    }

    @Test
    fun setGame() {
        numberPlaceViewModel.setGame(mockk(relaxed = true))
    }

    @Test
    fun setCorrect() {
        numberPlaceViewModel.setCorrect()

        assertFalse(numberPlaceViewModel.loading().value)
    }

    @Test
    fun masked() {
        assertNotNull(numberPlaceViewModel.masked())
    }

    @Test
    fun place() {
        numberPlaceViewModel.place(0, 0, 1, {})
    }

    @Test
    fun useHint() {
        numberPlaceViewModel.useHint(0, 0, mutableStateOf(""), {})
    }

    @Test
    fun saveCurrentGame() {
        mockkConstructor(GameFileProvider::class)
        every { anyConstructed<GameFileProvider>().invoke() } returns mockk()

        numberPlaceViewModel.saveCurrentGame()

        verify { repository.save(any(), any()) }
    }

    @Test
    fun pickSolving() {
        numberPlaceViewModel.pickSolving(0, 0)
    }

    @Test
    fun deleteGame() {
        mockkConstructor(GameFileProvider::class)
        every { anyConstructed<GameFileProvider>().invoke() } returns mockk()

        numberPlaceViewModel.deleteGame()

        verify { anyConstructed<GameFileProvider>().invoke() }
        verify { repository.delete(any()) }
    }

    @Test
    fun deleteGameProvidedNullCase() {
        mockkConstructor(GameFileProvider::class)
        every { anyConstructed<GameFileProvider>().invoke() } returns null

        numberPlaceViewModel.deleteGame()

        verify { anyConstructed<GameFileProvider>().invoke() }
        verify { repository wasNot called }
    }

}