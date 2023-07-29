package jp.toastkid.yobidashi4.presentation.viewmodel.web

import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.presentation.web.event.WebTabEvent
import kotlinx.coroutines.flow.SharedFlow

interface WebTabViewModel {
    fun event(): SharedFlow<WebTabEvent>
    fun openFind(): Boolean
    fun switchFind()
    fun inputValue(): TextFieldValue
    fun onFindInputChange(id: String, value: TextFieldValue)
    fun findUp(id: String)
    fun findDown(id: String)
    fun switchDevTools(id: String)
    fun reload(id: String)
}