package jp.toastkid.yobidashi4.presentation.tool.notification.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.domain.repository.notification.NotificationEventRepository
import jp.toastkid.yobidashi4.domain.service.notification.ScheduledNotification
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationListTabViewModel : KoinComponent {

    private val notification: ScheduledNotification by inject()

    private val focusRequester = FocusRequester()
    fun focusRequester() = focusRequester

    private val state = LazyListState()
    fun listState() = state

    private val scrollAction = KeyboardScrollAction(state)
    fun onKeyEvent(coroutineScope: CoroutineScope, it: KeyEvent): Boolean {
        return scrollAction.invoke(coroutineScope, it.key, it.isCtrlPressed)
    }

    private val notificationEvents = mutableStateListOf<NotificationEvent>()
    fun items(): List<NotificationEvent> = notificationEvents

    private val repository: NotificationEventRepository by inject()

    private val mainViewModel: MainViewModel by inject()

    fun add() {
        val new = NotificationEvent.makeDefault()
        repository
            .add(new)
        notificationEvents.add(new)
    }

    fun update(index: Int, title: String, text: String, dateInput: String) {
        val dateTime = NotificationEvent.parse(dateInput) ?: return

        val notificationEvent = NotificationEvent(title, text, dateTime)
        repository.update(index, notificationEvent)
        mainViewModel.showSnackbar("Update notification event.")

        CoroutineScope(Dispatchers.IO).launch {
            notification.start()
        }
    }

    fun deleteAt(index: Int) {
        repository
            .deleteAt(index)
        notificationEvents.removeAt(index)
        mainViewModel
            .showSnackbar("Delete notification event.")
    }

    fun start(dispatcher: CoroutineDispatcher) {
        CoroutineScope(dispatcher).launch {
            notificationEvents.addAll(repository.readAll())
        }
        focusRequester().requestFocus()
    }

}