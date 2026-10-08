/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.aggregation

import jp.toastkid.yobidashi4.domain.model.aggregation.StepsAggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import java.nio.file.Files
import java.util.regex.Pattern
import kotlin.io.path.nameWithoutExtension

class StepsAggregatorService(private val articlesReaderService: ArticlesReaderService) : ArticleAggregator {

    override operator fun invoke(keyword: String): StepsAggregationResult {
        val aggregationResult = StepsAggregationResult()
        articlesReaderService.invoke()
            .parallel()
            .filter { it.nameWithoutExtension.startsWith(keyword) }
            .map { it.nameWithoutExtension to Files.readAllLines(it) }
            .forEach {
                it.second.filter(::containsTarget)
                    .forEach { line ->
                        val matcher = pattern.matcher(line)
                        while (matcher.find()) {
                            aggregationResult.put(
                                it.first,
                                matcher.group(INDEX_STEPS).replace(",", "").toIntOrNull() ?: 0,
                                matcher.group(INDEX_CALORIE).toIntOrNull() ?: 0
                            )
                        }
                    }
            }
        return aggregationResult
    }

    private fun containsTarget(line: String): Boolean = line.contains(TARGET)

    override fun label() = "Steps"

    companion object {

        private const val TARGET = "今日の歩数は"

        private const val INDEX_STEPS = 1

        private const val INDEX_CALORIE = 2

        private val pattern = Pattern.compile("歩数は(.+?)、消費カロリーは(.+?)kcal")

    }

}