package jp.toastkid.yobidashi4.domain.repository.web.history

import jp.toastkid.yobidashi4.domain.model.web.history.WebHistory

interface WebHistoryRepository {

    fun add(title: String, url: String)

    fun delete(title: String, url: String)

    fun readAll(): List<WebHistory>

    fun clear()

}