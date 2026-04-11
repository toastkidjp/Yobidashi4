/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.clustering

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TfIdfDataSetPreparatorTest {

    private lateinit var preparator: TfIdfDataSetPreparator

    @BeforeEach
    fun setUp() {
        preparator = TfIdfDataSetPreparator()
    }

    @Test
    fun testInvokeWithValidData() {
        val docs = listOf(
            "id1" to "漢字漢字漢字漢字"
        )

        val dataset = preparator(docs)

        assertEquals(1, dataset.size(), "データセットには1つのドキュメントが含まれるべき")

        val example = dataset.getExample(0)

        val features = example.map { it.name }
        assertTrue(features.contains("漢字"), "3回以上出現する '漢字' は特徴量に含まれるべき")
    }

    @Test
    fun testFilteringLogic() {
        val docs = listOf(
            "id1" to "室温 室温 室温 室温" + // ストップワード
                    "ab ab ab ab" +    // 正規表現(漢字/ひらがな/カタカナ2文字)に不適合
                    "ああああ"          // 漢字を1文字も含まない (isKanjiCharacter)
        )

        val dataset = preparator(docs)
        println(dataset)

        assertEquals(0, dataset.size(), "None of results.")
    }

    @Test
    fun testTfIdfCalculation() {
        val docs = listOf(
            "id1" to "学習学習学習",
            "id2" to "学習学習学習"
        )

        val dataset = preparator(docs)
        val example = dataset.getExample(0)

        val featureValue = example.find { it.name == "学習" }?.value ?: 0.0

        val expected = 3 * kotlin.math.ln(2.0)

        assertEquals(expected, featureValue, 0.0001, "TF-IDFの計算値が一致すること")
    }

    @Test
    fun testEmptyInput() {
        val dataset = preparator(emptyList())
        assertEquals(0, dataset.size())
    }

}
