package jp.toastkid.yobidashi4.infrastructure.viewmodel.browser

import jp.toastkid.yobidashi4.domain.service.web.event.ReloadEvent
import jp.toastkid.yobidashi4.domain.service.web.event.SwitchDeveloperToolEvent
import jp.toastkid.yobidashi4.domain.service.web.event.WebTabEvent
import jp.toastkid.yobidashi4.presentation.viewmodel.web.WebTabViewModel
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