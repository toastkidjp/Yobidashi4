/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.aggregation

import jp.toastkid.yobidashi4.domain.model.aggregation.AggregationResult
import jp.toastkid.yobidashi4.domain.model.aggregation.ArticleLengthAggregationResult
import jp.toastkid.yobidashi4.domain.service.article.ArticlesReaderService
import java.nio.file.Files
import kotlin.io.path.nameWithoutExtension

class ArticleLengthAggregatorService(
    private val articlesReaderService: ArticlesReaderService
): ArticleAggregator {

    override operator fun invoke(keyword: String): AggregationResult {
        val result = ArticleLengthAggregationResult()

        articlesReaderService.invoke()
                .parallel()
                .filter { it.nameWithoutExtension.startsWith(keyword) }
                .map { it.nameWithoutExtension to Files.readAllBytes(it) }
                .forEach {
                    result.put(it.first, String(it.second).trim().codePoints().count())
                }

        return result
    }

    override fun label() = "Article length"

}
