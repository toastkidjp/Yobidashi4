package jp.toastkid.yobidashi4.presentation.main.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InputBoxViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val focusRequester = FocusRequester()

    private val query = TextFieldState()

    fun setShowInputBox() {
        viewModel.setShowInputBox()
    }

    fun invokeAction() {
        if (query.text.isBlank()) {
            return
        }

        viewModel.invokeInputAction(query.text.toString())
        setShowInputBox()
    }

    fun query() = query

    fun clearInput() {
        query.clearText()
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

        val webTab = viewModel.currentTab() as? WebTab ?: return
        query.setTextAndPlaceCursorAtEnd(webTab.url())
    }

}