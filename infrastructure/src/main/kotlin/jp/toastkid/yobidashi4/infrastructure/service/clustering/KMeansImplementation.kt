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
import org.tribuo.MutableDataset
import org.tribuo.clustering.ClusteringFactory
import org.tribuo.clustering.kmeans.KMeansTrainer
import org.tribuo.impl.ArrayExample
import org.tribuo.math.distance.CosineDistance
import org.tribuo.provenance.SimpleDataSourceProvenance
import java.time.OffsetDateTime
import kotlin.math.ln
import kotlin.math.sqrt

@Single
class KMeansImplementation : KMeans {

    private val stopWords = setOf("食べ", "を食", "室温")

    override fun invoke(docs: List<Pair<String, String>>): Map<String, List<String>> {
        val factory = ClusteringFactory()

        val provenance = SimpleDataSourceProvenance("empty-dataset", OffsetDateTime.now(), factory)

        val dataset = MutableDataset(provenance, factory)

        val dfMap = docs
            .flatMap { text -> makeBiGram(text).distinct() }
            .groupingBy { it }
            .eachCount()

        docs.forEach { text ->
            // ここで一旦、このドキュメント内の 2-gram の頻度を計算する
            val counts = makeBiGram(text)
                .groupingBy { it }
                .eachCount()
                .filter { it.value > 2 }

            val example = ArrayExample(factory.getUnknownOutput())

            // 集計済みの頻度を重み(Value)として add する
            counts.forEach { (word, tf) ->
                val df = dfMap[word] ?: 1
                // 標準的な TF-IDF (スムージング版)
                val tfIdf = tf * ln(docs.size / df.toDouble() + 1.0)

                if (tfIdf > 0.0) {
                    example.add(word, tfIdf)
                }
            }
            if (example.any()) {
                dataset.add(example)
            }
        }

        val k = (sqrt(docs.size.toDouble() / 2.0).toInt()).coerceIn(1, docs.size / 3)
        val trainer = KMeansTrainer(k, 10, CosineDistance(), 1, 42L)

        val model = trainer.train(dataset)

        val clusterNames = model.centroids.mapIndexed { i, centroid ->
            // 重心内の特徴量を取得し、スコアが高い順にソート
            val topFeatures = centroid
                .sortedByDescending { it.value }
                // 助詞などのノイズを簡易的に排除（2文字以下を捨てる、または特定の文字を含むものを捨てる）
                .filter { it.name.length >= 2 && !it.name.endsWith("の") && !it.name.endsWith("に") }
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

    private val regex = Regex("[\\p{IsHan}\\p{IsHira}\\p{IsKana}]{2}")

    private fun isKanjiCharacter(char: Char): Boolean = char in '\u4E00'..'\u9FFF'

    private fun makeBiGram(text: Pair<String, String>): List<String> = text.second
        .replace("\n", "")
        .windowed(2, 1)
        .filter(CharSequence::isNotBlank)
        .filter { it.matches(regex) }
        .filter { it.any(::isKanjiCharacter) }
        .filter { stopWords.none { stopWord -> it.contains(stopWord) } }

}
