package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.chat.GenerativeAiModel
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite
import jp.toastkid.yobidashi4.domain.service.tool.calculator.SimpleCalculator
import jp.toastkid.yobidashi4.presentation.lib.input.InputHistoryService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.DecimalFormat

class WebSearchBoxViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val setting: Setting by inject()

    private val focusRequester = FocusRequester()

    private val inputHistoryService = InputHistoryService("web_search")

    private val inputHistories = mutableStateListOf<InputHistory>()

    private val items = mutableStateListOf<WebSearchItem>()

    private val selectedSite = mutableStateOf(WebSearchItem.fromSearchSite(SearchSite.getDefault()))

    private val query = TextFieldState()

    private val openDropdown = mutableStateOf(false)

    private val calculator = SimpleCalculator()

    private val result = mutableStateOf("")

    private val saveSearchHistory = mutableStateOf(false)

    fun setShowWebSearch(open: Boolean) {
        viewModel.setShowWebSearch(open)
    }

    fun openingDropdown() = openDropdown.value

    fun setOpenDropdown() {
        openDropdown.value = true
    }

    fun closeDropdown() {
        openDropdown.value = false
    }

    fun items(): List<WebSearchItem> = items

    fun currentIconPath() = selectedSite.value.icon

    fun currentSiteName(): String? {
        return selectedSite.value.label
    }

    fun currentTint() = selectedSite.value.useTint

    fun containsSwingContent(): Boolean {
        return viewModel.currentTab() is WebTab
    }

    fun choose(it: WebSearchItem) {
        selectedSite.value = it
        closeDropdown()
    }

    fun onValueChange() {
        result.value = calculator.invoke(query.text.toString())?.let(formatter::format) ?: ""

        inputHistoryService.filter(inputHistories, query.text.toString())
    }

    fun query() = query

    fun invokeSearch() {
        if (query.text.isBlank() || query.composition != null) {
            return
        }

        if (query.text.startsWith("https://")) {
            viewModel.openUrl(query.text.toString(), false)
            viewModel.setShowWebSearch(false)
            return
        }

        selectedSite.value.action(viewModel, query.text.toString())
        viewModel.setShowWebSearch(false)

        if (saveSearchHistory()) {
            inputHistoryService.add(query.text.toString())
        }
    }

    fun clearInput() {
        query.clearText()
    }

    fun focusRequester() = focusRequester

    fun onKeyEvent(it: KeyEvent): Boolean {
        if (it.type != KeyEventType.KeyDown) {
            return false
        }

        if (it.key == Key.Escape) {
            viewModel.setShowWebSearch(false)
            return true
        }

        if (it.isCtrlPressed && it.key == Key.Two) {
            val newText = "\"${query.text}\""
            query.setTextAndPlaceCursorAtEnd(newText)
            return true
        }

        return false
    }

    fun existsResult(): Boolean {
        return result.value.isNotBlank()
    }

    fun result() = result.value

    fun showWebSearch(): Boolean {
        return viewModel.showWebSearch()
    }

    fun inputHistories(): List<String> = inputHistoryService.inputHistories(inputHistories)

    fun shouldShowInputHistory(): Boolean = inputHistoryService.shouldShowInputHistory(inputHistories)

    fun putText(text: String?) {
        val textFieldValue = inputHistoryService.make(text) ?: return
        query.setTextAndPlaceCursorAtEnd(textFieldValue)
        inputHistories.clear()
    }

    fun deleteInputHistoryItem(text: String) {
        inputHistoryService.delete(inputHistories, text)
    }

    fun clearInputHistory() {
        inputHistoryService.clear(inputHistories)
    }

    fun start() {
        if (viewModel.showWebSearch()) {
            focusRequester().requestFocus()
        }

        SearchSite.entries.map(WebSearchItem::fromSearchSite)
            .forEach(items::add)

        if (setting.chatApiKey().isNullOrBlank().not()) {
            GenerativeAiModel
                .entries
                .map(WebSearchItem::from)
                .forEach(items::add)
        }

        val webTab = viewModel.currentTab() as? WebTab ?: return
        query.setTextAndPlaceCursorAtEnd(webTab.url())
    }

    fun onFocusChanged(focusState: FocusState) {
        if (focusState.hasFocus) {
            return
        }

        inputHistories.clear()
    }

    fun makeVerticalOffset(): Dp {
        return (if (containsSwingContent()) -80 else 0).dp
    }

    fun saveSearchHistory(): Boolean {
        return saveSearchHistory.value
    }

    fun setSaveSearchHistory(b: Boolean) {
        saveSearchHistory.value = b
    }

    fun switchSaveSearchHistory() {
        saveSearchHistory.value = saveSearchHistory.value.not()
    }

    private val formatter = DecimalFormat("#,###.##")

}