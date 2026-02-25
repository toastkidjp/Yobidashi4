/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.clustering

import jp.toastkid.yobidashi4.domain.service.tool.clustering.KMeans
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KMeansImplementationTest {

    private lateinit var subject: KMeans

    @BeforeEach
    fun setUp() {
        subject = KMeansImplementation()
    }

    @Test
    fun invoke() {
        val result = subject.invoke(
            listOf(
                "test1" to "サイゼリヤの隣の客はよく柿食う客だと言われる。本当にそんなに塩柿を食うだろうか？".repeat(10),
                "test2" to "祇園精舎の鐘の音をあの客は聞いたらしい".repeat(10),
                "test3" to "松島やああ松島や松島やと芭蕉客員教授は言ったそうな".repeat(10),
                "test4" to "塩の向上とは何でしょうか？塩山さんは塩焼きをつつきながらそう言った".repeat(10),
                "test5" to "To be, or not to be, that is a question.".repeat(10),
            )
        )

        assertEquals(1, result.size)
        assertEquals(4, result.values.first().size)
    }

}