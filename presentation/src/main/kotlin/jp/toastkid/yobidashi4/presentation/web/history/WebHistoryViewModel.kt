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
import java.net.MalformedURLException
import java.net.URL
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import jp.toastkid.yobidashi4.domain.model.web.history.WebHistory
import jp.toastkid.yobidashi4.domain.model.web.icon.WebIcon
import jp.toastkid.yobidashi4.domain.repository.web.history.WebHistoryRepository
import jp.toastkid.yobidashi4.presentation.lib.KeyboardScrollAction
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.absolutePathString
import kotlin.io.path.pathString
import kotlinx.coroutines.CoroutineScope
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

    fun launch() {
        reloadItems()
        focusRequester().requestFocus()
    }

    private fun reloadItems() {
        list.clear()
        repository.readAll().sortedByDescending { it.lastVisitedTime }.forEach { list.add(it) }
    }

    fun list() = list

    fun openUrl(url: String, onBackground: Boolean) {
        viewModel.openUrl(url, onBackground)
    }

    fun findIconPath(history: WebHistory): String? {
        val host = extractHost(history) ?: return null

        return favicons.firstOrNull {
            val startsWith = it.fileName.pathString.startsWith(host)
            startsWith
        }?.absolutePathString()
    }

    private fun extractHost(bookmark: WebHistory): String? {
        return try {
            URL(bookmark.url).host.trim()
        } catch (e: MalformedURLException) {
            null
        }
    }

    fun dateTimeString(webHistory: WebHistory): String {
        return LocalDateTime
            .ofInstant(Instant.ofEpochMilli(webHistory.lastVisitedTime), ZoneId.systemDefault())
            .format(dateFormatter)
    }

    fun browseUri(url: String) {
        viewModel.browseUri(url)
    }

    fun delete(item: WebHistory) {
        repository.delete(item)
        reloadItems()
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

        viewModel.showSnackbar(
            "Done!",
            "Undo",
            {
                repository.storeAll(current)
                reloadItems()
            }
        )
    }

}