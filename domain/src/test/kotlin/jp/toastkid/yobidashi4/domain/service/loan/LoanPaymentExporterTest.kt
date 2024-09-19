package jp.toastkid.yobidashi4.domain.service.loan

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.io.BufferedWriter
import java.io.StringWriter
import java.nio.file.Files
import jp.toastkid.yobidashi4.domain.model.loan.Factor
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoanPaymentExporterTest {

    private lateinit var subject: LoanPaymentExporter

    private lateinit var writer: BufferedWriter

    @BeforeEach
    fun setUp() {
        subject = LoanPaymentExporter()

        mockkStatic(Files::class)
        every { Files.exists(any()) } returns true
        every { Files.createDirectories(any()) } returns mockk()
        writer = BufferedWriter(StringWriter())
        every { Files.newBufferedWriter(any()) } returns writer
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val factor = Factor(30_000_000, 35, 1.0, 1_000_000, 10000, 10000)
        subject.invoke(
            factor,
            LoanCalculator().invoke(factor)
        )

        verify { Files.exists(any()) }
        verify(inverse = true) { Files.createDirectories(any()) }
        println(writer.toString())
    }

    @Test
    fun withCreateDirectlyIfDoesNotExists() {
        every { Files.exists(any()) } returns false
        val factor = Factor(30_000_000, 35, 1.0, 1_000_000, 10000, 10000)

        subject.invoke(
            factor,
            LoanCalculator().invoke(factor)
        )

        verify { Files.exists(any()) }
        verify { Files.createDirectories(any()) }
    }

}