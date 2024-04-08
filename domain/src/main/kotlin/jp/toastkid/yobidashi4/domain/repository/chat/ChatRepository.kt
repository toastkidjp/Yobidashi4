package jp.toastkid.yobidashi4.domain.repository.chat

interface ChatRepository {

    fun request(content: String, streamLineConsumer: (String?) -> Unit)

}