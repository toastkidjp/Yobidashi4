/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.extension

import okio.Path.Companion.toPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class ExtensionTest {

    @ParameterizedTest
    @CsvSource(
        "test, ''",
        "test., ''",
        "., ''",
        ".., ''",
        "test.txt, txt",
        "test.txt.ext, ext",
    )
    fun getExtension(fileName: String, expected: String) {
        assertEquals(expected, fileName.toPath().extension)
    }

}