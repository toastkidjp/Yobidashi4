package jp.toastkid.yobidashi4.domain.repository.input

import jp.toastkid.yobidashi4.domain.model.input.InputHistory

interface InputHistoryRepository {

    fun list(): List<InputHistory>

    fun filter(query: String?): List<InputHistory>

    fun add(item: InputHistory)

    fun delete(item: InputHistory)

    fun deleteWithWord(word: String)

    fun clear()

}