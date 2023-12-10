package jp.toastkid.yobidashi4.domain.repository.notification

import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent

interface NotificationEventRepository {

    fun add(event: NotificationEvent)

    fun readAll(): List<NotificationEvent>

    fun update(index: Int, event: NotificationEvent)

    fun deleteAt(index: Int)

    fun clear()

}