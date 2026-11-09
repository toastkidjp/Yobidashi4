/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.tool.file

import okio.Path.Companion.toPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

class NumericPathComparatorImplementationTest {

    private lateinit var subject: NumericPathComparatorImplementation

    @BeforeEach
    fun setUp() {
        subject = NumericPathComparatorImplementation()
    }

    @Test
    fun compare() {
        val paths = listOf(
            "test_02",
            "test_01",
            "test_101",
            "test_15",
            "test_04"
        )
            .map { it.toPath().toNioPath() }
            .sortedWith(subject)
            .map(Path::nameWithoutExtension)

        assertAll(
            { assertEquals("test_01", paths[0]) },
            { assertEquals("test_02", paths[1]) },
            { assertEquals("test_04", paths[2]) },
            { assertEquals("test_15", paths[3]) },
            { assertEquals("test_101", paths[4]) }
        )
    }

}