/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.clustering

import jp.toastkid.yobidashi4.domain.service.tool.clustering.KMeans
import org.koin.core.annotation.Single
import org.tribuo.Feature
import org.tribuo.clustering.kmeans.KMeansTrainer
import org.tribuo.math.distance.CosineDistance
import kotlin.math.sqrt

@Single
class KMeansImplementation : KMeans {

    override fun invoke(docs: List<Pair<String, String>>): Map<String, List<String>> {
        val dataset = TfIdfDataSetPreparator().prepareDataSet(docs)

        val k = (sqrt(docs.size.toDouble() / 2.0).toInt()).coerceIn(1, docs.size / 3)
        val trainer = KMeansTrainer(k, 10, CosineDistance(), 1, 42L)

        val model = trainer.train(dataset)

        val clusterNames = model.centroids.mapIndexed { i, centroid ->
            // 重心内の特徴量を取得し、スコアが高い順にソート
            val topFeatures = centroid
                .sortedByDescending { it.value }
                .filter(::notContainsNoise)
                .take(3) // 上位3つをラベル候補にする
                .joinToString("/") { it.name }

            "Cluster $i ($topFeatures)"
        }

        val groupBy = dataset
            .mapIndexed { i, example -> model.predict(example).output.id to docs[i].first }
            .groupingBy { it.first }
            .fold(
                initialValueSelector = { _, _ -> mutableListOf<String>() },
                operation = { _, acc, element ->
                    acc.add(element.second)
                    acc
                }
            )
            .map { clusterNames[it.key] to it.value }
            .toMap()

        /*
        model.centroids.forEachIndexed { i, centroid ->
            println("Cluster $i features: ${centroid.size}")
            // 重みが大きい上位 5 つを表示
            centroid
                .sortedByDescending { it.value }
                .take(5)
                .forEachIndexed { i, cluster -> println("  ${clusterNames[i]}: ${cluster.value}") }
        }
         */

        return groupBy
    }

    /**
     * 助詞などのノイズを簡易的に排除（2文字以下を捨てる、または特定の文字を含むものを捨てる）.
     */
    private fun notContainsNoise(feature: Feature): Boolean =
        feature.name.length >= 2 && !feature.name.endsWith("の") && !feature.name.endsWith("に")

}
