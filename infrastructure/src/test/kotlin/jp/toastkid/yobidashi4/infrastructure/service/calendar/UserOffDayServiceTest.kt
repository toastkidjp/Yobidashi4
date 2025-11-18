/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.calendar

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * TODO Fix it
 */
internal class UserOffDayServiceTest {

    private lateinit var userOffDayService: UserOffDayServiceImplementation

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { setting.userOffDay() } returns listOf(2 to 22, 12 to 29, 12 to 30, 12 to 31)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        userOffDayService = UserOffDayServiceImplementation()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @ParameterizedTest
    @CsvSource(
        "12, 3, false",
        "11, 29, false",
        "12, 29, true",
    )
    fun testInvoke(month: Int, day: Int, expected: Boolean) {
        assertEquals(expected, userOffDayService.invoke(month, day))
    }

    @Test
    fun findBy() {
        assertTrue(userOffDayService.findBy(12).isNotEmpty())
        assertTrue(userOffDayService.findBy(6).isEmpty())
    }

}