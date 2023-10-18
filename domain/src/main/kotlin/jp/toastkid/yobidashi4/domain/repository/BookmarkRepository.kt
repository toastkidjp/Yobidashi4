package jp.toastkid.yobidashi4.domain.repository

import jp.toastkid.yobidashi4.domain.model.web.bookmark.Bookmark

interface BookmarkRepository {

    fun list(): List<Bookmark>

    fun add(item: Bookmark)

    fun delete(item: Bookmark)

}