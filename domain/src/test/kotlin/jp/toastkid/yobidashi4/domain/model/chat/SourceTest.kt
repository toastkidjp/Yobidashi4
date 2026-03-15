package jp.toastkid.yobidashi4.domain.model.chat/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SourceTest {
    @Test
    fun testHost() {
        assertEquals(
            "test.co.jp",
            Source(
                "test.co.jp",
                "https://vertexaisearch.cloud.google.com/test"
            ).host()
        )
        assertEquals(
            "test.co.jp",
            Source(
                "usage",
                "https://test.co.jp/test"
            ).host()
        )
    }

}