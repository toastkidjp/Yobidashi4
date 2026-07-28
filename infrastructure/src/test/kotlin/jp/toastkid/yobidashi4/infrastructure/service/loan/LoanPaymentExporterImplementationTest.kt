/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.loan

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.loan.Factor
import jp.toastkid.yobidashi4.domain.service.loan.LevelPaymentCalculator
import jp.toastkid.yobidashi4.domain.service.loan.LoanPaymentExporter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.BufferedWriter
import java.io.StringWriter
import java.nio.file.Files

class LoanPaymentExporterImplementationTest {

    private lateinit var subject: LoanPaymentExporter

    private val calculator = LevelPaymentCalculator()

    private lateinit var writer: BufferedWriter

    @BeforeEach
    fun setUp() {
        subject = LoanPaymentExporterImplementation()

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
            calculator.invoke(factor)
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
            calculator.invoke(factor)
        )

        verify { Files.exists(any()) }
        verify { Files.createDirectories(any()) }
    }

}