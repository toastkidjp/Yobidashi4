package jp.toastkid.yobidashi4.presentation.web.history

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import jp.toastkid.yobidashi4.domain.model.tab.WebHistoryTab
import jp.toastkid.yobidashi4.domain.model.web.history.WebHistory
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.repository.web.history.WebHistoryRepository
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import jp.toastkid.yobidashi4.presentation.lib.clipboard.ClipboardPutterService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WebHistoryViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val repository: WebHistoryRepository by inject()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd(E)HH:mm:ss").withLocale(Locale.ENGLISH)

    private val list = mutableStateListOf<WebHistory>()

    private val favicons = WebIcon().readAll()

    private val state = LazyListState()

    private val focusRequester = FocusRequester()

    private val scrollAction = KeyboardScrollAction(state)

    fun listState() = state

    fun focusRequester() = focusRequester

    fun scrollAction(coroutineScope: CoroutineScope, key: Key, controlDown: Boolean) =
        scrollAction.invoke(coroutineScope, key, controlDown)

    fun launch(coroutineScope: CoroutineScope, tab: WebHistoryTab) {
        reloadItems()
        focusRequester().requestFocus()
        coroutineScope.launch {
            state.scrollToItem(tab.scrollPosition())
        }
    }

    private fun reloadItems() {
        list.clear()
        repository.readAll().sortedByDescending { it.lastVisitedTime }.forEach { list.add(it) }
    }

    fun list(): List<WebHistory> = list

    fun openUrl(url: String, onBackground: Boolean) {
        viewModel.openUrl(url, onBackground)
    }

    fun dateTimeString(webHistory: WebHistory): String {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(webHistory.lastVisitedTime), ZoneId.systemDefault())
            .format(dateFormatter)
    }

    fun browseUri(url: String) {
        viewModel.browseUri(url)
        closeDropdown()
    }

    fun delete(item: WebHistory) {
        repository.delete(item)
        reloadItems()
        closeDropdown()
    }

    private val currentDropdownItem = mutableStateOf<WebHistory?>(null)

    fun openingDropdown(item: WebHistory) = item == currentDropdownItem.value

    fun openDropdown(item: WebHistory) {
        currentDropdownItem.value = item
    }

    fun closeDropdown() {
        currentDropdownItem.value = null
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun onPointerEvent(pointerEvent: PointerEvent, webHistory: WebHistory) {
        if (pointerEvent.type == PointerEventType.Press
            && !openingDropdown(webHistory)
            && pointerEvent.button == PointerButton.Secondary
        ) {
            openDropdown(webHistory)
        }
    }

    fun clear() {
        val current = mutableListOf<WebHistory>().also {
            it.addAll(list)
        }

        list.clear()
        repository.clear()

        viewModel.showSnackbar("Done!", "Undo") {
            repository.storeAll(current)
            reloadItems()
        }
        closeDropdown()
    }

    fun onDispose(tab: WebHistoryTab) {
        viewModel.updateScrollableTab(tab, state.firstVisibleItemIndex)
    }

    fun clipText(text: String) {
        ClipboardPutterService().invoke(text)
        closeDropdown()
    }

}