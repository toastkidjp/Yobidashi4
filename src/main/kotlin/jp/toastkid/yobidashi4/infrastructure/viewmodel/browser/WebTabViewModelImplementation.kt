package jp.toastkid.yobidashi4.infrastructure.viewmodel.browser

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.presentation.viewmodel.web.WebTabViewModel
import jp.toastkid.yobidashi4.presentation.web.event.FindEvent
import jp.toastkid.yobidashi4.presentation.web.event.ReloadEvent
import jp.toastkid.yobidashi4.presentation.web.event.SwitchDeveloperToolEvent
import jp.toastkid.yobidashi4.presentation.web.event.WebTabEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class WebTabViewModelImplementation : WebTabViewModel {

    private val _event = MutableSharedFlow<WebTabEvent>()

    override fun event() = _event.asSharedFlow()

    private val openFind = mutableStateOf(false)

    private val findInput = mutableStateOf(TextFieldValue())

    override fun openFind() = openFind.value

    override fun switchFind() {
        openFind.value = openFind.value.not()
    }

    override fun inputValue() = findInput.value

    override fun onFindInputChange(id: String, value: TextFieldValue) {
        findInput.value = TextFieldValue(value.text, value.selection, value.composition)
        findDown(id)
    }

    override fun findUp(id: String) {
        CoroutineScope(Dispatchers.Default).launch {
            _event.emit(FindEvent(id, inputValue().text, true))
        }
    }

    override fun findDown(id: String) {
        CoroutineScope(Dispatchers.Default).launch {
            _event.emit(FindEvent(id, inputValue().text))
        }
    }

    override fun switchDevTools(id: String) {
        CoroutineScope(Dispatchers.Default).launch {
            _event.emit(SwitchDeveloperToolEvent(id))
        }
    }

    override fun reload(id: String) {
        CoroutineScope(Dispatchers.Default).launch {
            _event.emit(ReloadEvent(id))
        }
    }

}