/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.clustering

import org.tribuo.MutableDataset
import org.tribuo.clustering.ClusterID
import org.tribuo.clustering.ClusteringFactory
import org.tribuo.impl.ArrayExample
import org.tribuo.provenance.SimpleDataSourceProvenance
import java.time.OffsetDateTime
import kotlin.math.ln

class TfIdfDataSetPreparator(
    private val bigramGenerator: BigramGenerator = BigramGenerator()
) {

    operator fun invoke(docs: List<Pair<String, String>>): MutableDataset<ClusterID?> {
        val factory = ClusteringFactory()

        val provenance = SimpleDataSourceProvenance("empty-dataset", OffsetDateTime.now(), factory)

        val dataset = MutableDataset(provenance, factory)

        val dfMap = docs
            .flatMap { text -> bigramGenerator.invoke(text).distinct() }
            .groupingBy { it }
            .eachCount()

        docs.forEach { text ->
            // ここで一旦、このドキュメント内の 2-gram の頻度を計算する
            val example = calculateExample(text, factory, dfMap, docs)
            if (example.any()) {
                dataset.add(example)
            }
        }
        return dataset
    }

    private fun calculateExample(
        text: Pair<String, String>,
        factory: ClusteringFactory,
        dfMap: Map<String, Int>,
        docs: List<Pair<String, String>>
    ): ArrayExample<ClusterID?> {
        val counts = bigramGenerator.invoke(text)
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 2 }

        val example = ArrayExample(factory.unknownOutput)

        // 集計済みの頻度を重み(Value)として add する
        counts.forEach { (word, tf) ->
            val df = dfMap[word] ?: 1
            // 標準的な TF-IDF (スムージング版)
            val tfIdf = tf * ln(docs.size / df.toDouble() + 1.0)

            if (tfIdf > 0.0) {
                example.add(word, tfIdf)
            }
        }
        return example
    }

}
