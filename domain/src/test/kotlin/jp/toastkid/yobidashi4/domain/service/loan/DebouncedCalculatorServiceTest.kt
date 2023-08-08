package jp.toastkid.yobidashi4.domain.service.loan

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.loan.Factor
import jp.toastkid.yobidashi4.domain.model.loan.LoanPayment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DebouncedCalculatorServiceTest {

    @InjectMockKs
    private lateinit var debouncedCalculatorService: DebouncedCalculatorService

    private val inputChannel: Channel<String> = Channel()

    @MockK
    private lateinit var currentFactorProvider: () -> Factor

    @MockK
    private lateinit var onResult: (LoanPayment) -> Unit

    @MockK
    private lateinit var calculator: LoanCalculator

    private val debounceMillis: Long = 1000

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Unconfined

    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Unconfined

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { currentFactorProvider() } returns Factor(
            100000000,
            35,
            1.0,
            1000000,
            10000,
            10000
        )
        every { calculator.invoke(any()) } returns mockk()
        every { onResult(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        debouncedCalculatorService.invoke()

        CoroutineScope(Dispatchers.Unconfined).launch {
            inputChannel.send("test")

            verify { currentFactorProvider() }
            verify { calculator.invoke(any()) }
            verify { onResult(any()) }
        }
    }
}