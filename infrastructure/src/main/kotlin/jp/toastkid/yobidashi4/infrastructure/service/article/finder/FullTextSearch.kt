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
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.SearcherManager
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.FSDirectory
import java.io.Closeable
import java.io.IOException
import java.nio.file.Path
import java.text.ParseException

class FullTextSearch(private val searcherManager: SearcherManager) : Closeable {

    private val queryParser: QueryParser = QueryParser("content", StandardAnalyzer())

    @Throws(IOException::class, ParseException::class)
    fun search(searchQueryInput: String): TopDocs? {
        searcherManager.maybeRefresh()

        val sanitized =
            if (searchQueryInput.startsWith("?") || searchQueryInput.startsWith("*")) "\\$searchQueryInput"
            else searchQueryInput

        val searcher = searcherManager.acquire()
        return try {
            searcher.search(queryParser.parse(sanitized), 300)
        } finally {
            searcherManager.release(searcher)
        }
    }

    @Throws(IOException::class)
    fun getDocument(scoreDoc: ScoreDoc): Document? {
        val searcher = searcherManager.acquire()
        return try {
            searcher.storedFields().document(scoreDoc.doc)
        } finally {
            searcherManager.release(searcher)
        }
    }

    override fun close() {
        searcherManager.close()
    }

    companion object {
        fun make(indexDirectoryPath: Path): FullTextSearch {
            val directory = FSDirectory.open(indexDirectoryPath)
            // Directory から SearcherManager を作成
            val manager = SearcherManager(directory, null)
            return FullTextSearch(manager)
        }
    }

}