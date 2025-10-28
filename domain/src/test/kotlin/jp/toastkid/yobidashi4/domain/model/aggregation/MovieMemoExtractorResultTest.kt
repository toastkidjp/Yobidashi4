/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.aggregation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class MovieMemoExtractorResultTest {

    @Test
    fun itemArrays() {
        val result = MovieMemoExtractorResult()
        result.add("2025-01-01", "test")

        val itemArrays = result.itemArrays()
        assertEquals(1, result.itemArrays().size)
        assertSame(itemArrays, result.itemArrays())
    }

}