package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.pathString
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

class FullTextSearchIndexer(indexDirectoryPath: Path) {

    private val directory: Directory = FSDirectory.open(indexDirectoryPath)

    private val writer = IndexWriter(directory, IndexWriterConfig(StandardAnalyzer()))

    @Throws(IOException::class)
    private fun getDocument(path: Path): Document {
        val document = Document()
        val contentField = TextField("contents", Files.readString(path), Field.Store.NO)
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
        Files.list(dataDirPath)
            .filter { Files.isDirectory(it).not() && Files.exists(it) && Files.isReadable(it) }
            .forEach(::indexFile)
        writer.commit()
        return writer.numRamDocs()
    }

    fun close() {
        writer.close()
    }

}