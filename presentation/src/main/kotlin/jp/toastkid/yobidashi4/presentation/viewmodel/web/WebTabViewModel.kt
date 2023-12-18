package jp.toastkid.yobidashi4.presentation.viewmodel.web

import jp.toastkid.yobidashi4.presentation.web.event.WebTabEvent
import kotlinx.coroutines.flow.SharedFlow

interface WebTabViewModel {
    fun event(): SharedFlow<WebTabEvent>
    fun switchDevTools(id: String)
    fun reload(id: String)
}