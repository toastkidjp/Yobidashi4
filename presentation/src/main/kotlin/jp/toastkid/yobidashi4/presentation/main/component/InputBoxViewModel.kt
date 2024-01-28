package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InputBoxViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val focusRequester = FocusRequester()

    private val query = mutableStateOf(TextFieldValue())

    fun setShowInputBox() {
        viewModel.setShowInputBox()
    }

    fun invokeAction() {
        if (query.value.text.isBlank()) {
            return
        }

        viewModel.invokeInputAction(query.value.text)
        setShowInputBox()
    }

    fun onValueChange(it: TextFieldValue) {
        query.value = it
    }

    fun query() = query.value

    fun clearInput() {
        query.value = TextFieldValue()
    }

    fun focusRequester() = focusRequester

    fun onKeyEvent(it: KeyEvent): Boolean {
        if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
            viewModel.setShowInputBox()
            return true
        }
        return false
    }

    fun showInputBox(): Boolean {
        return viewModel.showInputBox()
    }

    fun start() {
        if (viewModel.showInputBox()) {
            focusRequester().requestFocus()
        }
        query.value = TextFieldValue((viewModel.currentTab() as? WebTab)?.url() ?: "")
    }

}