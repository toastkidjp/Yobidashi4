package jp.toastkid.yobidashi4.presentation.main.component

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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite
import jp.toastkid.yobidashi4.domain.service.tool.calculator.SimpleCalculator
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_amazon
import jp.toastkid.yobidashi4.library.resources.ic_filmarks
import jp.toastkid.yobidashi4.library.resources.ic_github
import jp.toastkid.yobidashi4.library.resources.ic_google_map
import jp.toastkid.yobidashi4.library.resources.ic_image
import jp.toastkid.yobidashi4.library.resources.ic_site_search
import jp.toastkid.yobidashi4.library.resources.ic_video
import jp.toastkid.yobidashi4.library.resources.ic_wikipedia
import jp.toastkid.yobidashi4.library.resources.ic_yahoo_japan_image_search
import jp.toastkid.yobidashi4.library.resources.ic_yahoo_japan_logo
import jp.toastkid.yobidashi4.library.resources.ic_yahoo_japan_realtime_search
import jp.toastkid.yobidashi4.presentation.lib.input.InputHistoryService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WebSearchBoxViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val focusRequester = FocusRequester()

    private val inputHistoryService = InputHistoryService("web_search")

    private val inputHistories = mutableStateListOf<InputHistory>()

    private val selectedSite = mutableStateOf(SearchSite.getDefault())

    private val query = mutableStateOf(TextFieldValue())

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

    fun currentIconPath() = icon(selectedSite.value)

    fun currentSiteName(): String? {
        return selectedSite.value.siteName
    }

    fun containsSwingContent(): Boolean {
        return viewModel.currentTab() is WebTab
    }

    fun choose(it: SearchSite) {
        selectedSite.value = it
        closeDropdown()
    }

    fun onValueChange(it: TextFieldValue) {
        query.value = it
        val calculatorResult = calculator.invoke(query.value.text)
        val toString = calculatorResult?.toString()
        result.value = when {
            toString == null -> ""
            toString.endsWith(".0") -> toString.substring(0, toString.lastIndexOf("."))
            else -> toString
        }

        inputHistoryService.filter(inputHistories, it.text)
    }

    fun query(): TextFieldValue {
        return query.value
    }

    fun invokeSearch() {
        if (query.value.text.isBlank() || query.value.composition != null) {
            return
        }

        if (query.value.text.startsWith("https://")) {
            viewModel.openUrl(query.value.text, false)
            viewModel.setShowWebSearch(false)
            return
        }

        selectedSite.value.make(query.value.text, (viewModel.currentTab() as? WebTab)?.url()).let {
            viewModel.openUrl(it.toString(), false)
        }
        viewModel.setShowWebSearch(false)

        if (saveSearchHistory()) {
            inputHistoryService.add(query.value.text)
        }
    }

    fun clearInput() {
        query.value = TextFieldValue()
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
            val newText = "\"${query.value.text}\""
            query.value = TextFieldValue(newText, TextRange(newText.length))
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

    fun shouldShowInputHistory() = inputHistoryService.shouldShowInputHistory(inputHistories)

    fun putText(text: String?) {
        val textFieldValue = inputHistoryService.make(text) ?: return
        query.value = textFieldValue
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

        val webTab = viewModel.currentTab() as? WebTab ?: return
        query.value = TextFieldValue(webTab.url())
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

    fun icon(searchSite: SearchSite) = when(searchSite) {
        SearchSite.YAHOO_JAPAN -> Res.drawable.ic_yahoo_japan_logo
        SearchSite.WIKIPEDIA -> Res.drawable.ic_wikipedia
        SearchSite.GOOGLE_MAP -> Res.drawable.ic_google_map
        SearchSite.IMAGE_YAHOO_JAPAN -> Res.drawable.ic_yahoo_japan_image_search
        SearchSite.YOUTUBE -> Res.drawable.ic_video
        SearchSite.REALTIME_YAHOO_JAPAN -> Res.drawable.ic_yahoo_japan_realtime_search
        SearchSite.FILMARKS -> Res.drawable.ic_filmarks
        SearchSite.AMAZON -> Res.drawable.ic_amazon
        SearchSite.GITHUB -> Res.drawable.ic_github
        SearchSite.SEARCH_WITH_IMAGE -> Res.drawable.ic_image
        SearchSite.SITE_SEARCH -> Res.drawable.ic_site_search
    }
    
}