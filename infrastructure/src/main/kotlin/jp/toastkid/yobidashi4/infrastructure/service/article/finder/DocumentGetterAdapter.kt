package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import org.apache.lucene.index.StoredFields
import org.apache.lucene.search.IndexSearcher

class DocumentGetterAdapter(private val indexSearcher: IndexSearcher) {

    operator fun invoke(): StoredFields {
        return indexSearcher.indexReader.storedFields()
    }

}