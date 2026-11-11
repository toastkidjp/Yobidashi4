/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.drop

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path

class DroppedPathReceiverActionTest {

    @InjectMockKs
    private lateinit var subject: DroppedPathReceiverAction

    @MockK
    private lateinit var onReceive: (Path) -> Unit

    @MockK
    private lateinit var onComplete: () -> Unit

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { onReceive.invoke(any()) } just Runs
        every { onComplete.invoke() } just Runs
    }

    @Test
    fun onReceive() {
        subject.onReceive(mockk())

        verify { onReceive.invoke(any()) }
    }

    @Test
    fun onComplete() {
        subject.onComplete()

        verify { onComplete.invoke() }
    }

}