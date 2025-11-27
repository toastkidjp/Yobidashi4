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
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.Term
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.pathString

class FullTextSearchIndexer(private val indexDirectoryPath: Path) {

    private val directory: Directory = FSDirectory.open(indexDirectoryPath)

    private val writer = IndexWriter(directory, IndexWriterConfig(StandardAnalyzer()))

    @Throws(IOException::class)
    private fun getDocument(path: Path): Document {
        val document = Document()
        val contentField = TextField("content", Files.readString(path), Field.Store.YES)
        val fileNameField = StringField("name", path.nameWithoutExtension, Field.Store.YES)
        val filePathField = StringField("path", path.pathString, Field.Store.YES)
        document.add(contentField)
        document.add(fileNameField)
        document.add(filePathField)
        return document
    }

    @Throws(IOException::class)
    private fun indexFile(path: Path) {
        writer.updateDocument(Term("path", path.pathString), getDocument(path))
    }

    @Throws(IOException::class)
    fun createIndex(dataDirPath: Path): Int {
        val indexTargetFilter = IndexTargetFilter(indexDirectoryPath)
        Files.list(dataDirPath)
            .filter(indexTargetFilter::invoke)
            .forEach(::indexFile)
        writer.commit()
        return writer.numRamDocs()
    }

    fun close() {
        writer.close()
    }

}