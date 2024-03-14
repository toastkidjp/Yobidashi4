package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.domain.model.web.search.SearchSite
import jp.toastkid.yobidashi4.domain.repository.input.InputHistoryRepository
import jp.toastkid.yobidashi4.domain.service.tool.calculator.SimpleCalculator
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.ParametersHolder

class WebSearchBoxViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val focusRequester = FocusRequester()

    private val inputHistoryRepository: InputHistoryRepository by inject(parameters = { ParametersHolder(mutableListOf("web_search")) })

    private val inputHistories = mutableStateListOf<InputHistory>()

    private val selectedSite = mutableStateOf(SearchSite.getDefault())

    private val query = mutableStateOf(TextFieldValue())

    private val openDropdown = mutableStateOf(false)

    private val calculator = SimpleCalculator()

    private val result = mutableStateOf("")

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

    fun currentIconPath(): String {
        return selectedSite.value.iconPath()
    }

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

        inputHistories.clear()
        inputHistories.addAll(inputHistoryRepository.filter(it.text).takeLast(5))
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
        inputHistoryRepository.add(InputHistory(query.value.text, System.currentTimeMillis()))
    }

    fun clearInput() {
        query.value = TextFieldValue()
    }

    fun focusRequester() = focusRequester

    fun onKeyEvent(it: KeyEvent): Boolean {
        if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
            viewModel.setShowWebSearch(false)
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

    fun inputHistories(): List<String> = inputHistories.map { it.word }

    fun shouldShowInputHistory() = inputHistories.isNotEmpty()

    fun putText(text: String?) {
        if (text.isNullOrBlank()) {
            return
        }
        query.value = TextFieldValue("${text} ", TextRange(text.length + 1))
        inputHistories.clear()
    }

    fun start() {
        if (viewModel.showWebSearch()) {
            focusRequester().requestFocus()
        }
        query.value = TextFieldValue(
            (viewModel.currentTab() as? WebTab)?.url() ?: ""
        )
    }

}