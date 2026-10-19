/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar.holiday

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LabelTest {

    @Test
    fun dayOfMonth() {
        assertEquals(1, Holiday("test", 1, 1).dayOfMonth())
    }

}