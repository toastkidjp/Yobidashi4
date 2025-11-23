/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.CorruptIndexException
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.FSDirectory
import java.io.IOException
import java.nio.file.Path
import java.text.ParseException

class FullTextSearch(private val indexSearcher: IndexSearcher) {

    private val queryParser: QueryParser = QueryParser("content", StandardAnalyzer())

    private val indexReader = indexSearcher.indexReader

    @Throws(IOException::class, ParseException::class)
    fun search(searchQueryInput: String): TopDocs? {
        val sanitized =
            if (searchQueryInput.startsWith("?") || searchQueryInput.startsWith("*")) "\\$searchQueryInput"
            else searchQueryInput
        return indexSearcher.search(queryParser.parse(sanitized), 300)
    }

    @Throws(CorruptIndexException::class, IOException::class)
    fun getDocument(scoreDoc: ScoreDoc): Document? {
        return indexReader.storedFields().document(scoreDoc.doc)
    }

    companion object {

        fun make(indexDirectoryPath: Path) = FullTextSearch(
            IndexSearcher(DirectoryReader.open(FSDirectory.open(indexDirectoryPath)))
        )

    }

}