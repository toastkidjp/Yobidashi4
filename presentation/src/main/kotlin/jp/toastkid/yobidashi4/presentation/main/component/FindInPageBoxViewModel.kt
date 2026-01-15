package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.lib.input.InputHistoryService
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FindInPageBoxViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val focusRequester = FocusRequester()

    private val findInputHistoryService = InputHistoryService("find_in_page")

    private val findInputHistories = mutableListOf<InputHistory>()

    fun onKeyEvent(it: KeyEvent): Boolean {
        if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
            viewModel.switchFind()
            return true
        }
        return false
    }

    fun switchFind() {
        viewModel.switchFind()
    }

    fun inputValue(): TextFieldState {
        return viewModel.inputValue()
    }

    fun onFindInputChange() {
        findInputHistoryService.filter(findInputHistories, inputValue().text.toString())
    }

    fun shouldShowInputHistory(): Boolean {
        return findInputHistoryService.shouldShowInputHistory(findInputHistories)
    }

    fun inputHistories(): List<String> {
        return findInputHistoryService.inputHistories(findInputHistories)
    }

    fun onClickInputHistory(it: String) {
        val value = findInputHistoryService.make(it) ?: return
        viewModel.inputValue().setTextAndPlaceCursorAtEnd(value)
        findInputHistories.clear()
    }

    fun onClickDelete(it: String) {
        findInputHistoryService.delete(findInputHistories, it)
    }

    fun onClickClear() {
        findInputHistoryService.clear(findInputHistories)
    }

    fun onFocusChanged(it: FocusState) {
        if (it.hasFocus) {
            return
        }

        findInputHistories.clear()
    }

    fun focusRequester(): FocusRequester {
        return focusRequester
    }

    fun useReplace(): Boolean {
        return viewModel.currentTab() is EditorTab
    }

    fun replaceInputValue() = viewModel.replaceInputValue()

    fun caseSensitive(): Boolean {
        return viewModel.caseSensitive()
    }

    fun switchCaseSensitive() {
        viewModel.switchCaseSensitive()
    }

    fun findUp() {
        viewModel.findUp()
    }

    fun findDown() {
        viewModel.findDown()
    }

    fun replaceAll() {
        viewModel.replaceAll()
    }

    fun findStatus(): String {
        return viewModel.findStatus()
    }

    fun openFind(): Boolean {
        return viewModel.openFind()
    }

    fun launch() {
        focusRequester().requestFocus()
    }

}