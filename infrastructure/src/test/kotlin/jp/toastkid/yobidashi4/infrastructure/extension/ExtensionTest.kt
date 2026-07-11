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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExtensionTest {

    @Test
    fun getExtension() {
        assertTrue("test".toPath().extension.isEmpty())
        assertTrue("test.".toPath().extension.isEmpty())
        assertEquals("txt", "test.txt".toPath().extension)
        assertEquals("exe", "test.txt.exe".toPath().extension)
        assertTrue(".".toPath().extension.isEmpty())
        assertTrue("..".toPath().extension.isEmpty())
    }

}