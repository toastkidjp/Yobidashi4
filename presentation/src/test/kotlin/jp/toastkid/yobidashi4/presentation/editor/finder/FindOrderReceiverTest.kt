/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.editor.finder

import androidx.compose.foundation.text.input.TextFieldState
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.find.FindOrder
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class FindOrderReceiverTest {

    private lateinit var subject: FindOrderReceiver

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier=null) { mainViewModel } bind(MainViewModel::class)
                }
            )
        }

        subject = FindOrderReceiver()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun invoke() {
        every { mainViewModel.setFindStatus(any()) } just Runs
        every { mainViewModel.finderFlow() } returns flowOf(
            FindOrder.EMPTY,
            FindOrder("test", ""),
            FindOrder("test", "", true, true, false)
        )

        val content = TextFieldState("test")
        subject.invoke(FindOrder.EMPTY, content)

        subject.invoke(FindOrder("test", ""), TextFieldState("test"))

        subject.invoke(FindOrder("test", "", true, true, false), TextFieldState("test"))
        verify { mainViewModel.setFindStatus(any()) }

        subject.invoke(FindOrder("", "", true, false, false), TextFieldState("test"))

        subject.invoke(FindOrder("at", ""), TextFieldState("test"))
    }

}