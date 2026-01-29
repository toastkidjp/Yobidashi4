package jp.toastkid.yobidashi4.presentation.setting

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path

class SettingEditorViewModel : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val setting: Setting by inject()

    private val focusRequester = FocusRequester()

    private val listState = LazyListState()

    private val items = mutableStateListOf<Pair<String, TextFieldState>>()

    fun onKeyEvent(coroutineScope: CoroutineScope, keyEvent: KeyEvent): Boolean {
        if (keyEvent.type != KeyEventType.KeyDown) {
            return false
        }

        return when {
            keyEvent.isCtrlPressed && keyEvent.key == Key.O -> {
                openFile()
                return true
            }
            keyEvent.isCtrlPressed && keyEvent.key == Key.S -> {
                save()
                return true
            }
            else -> false
        }
    }

    fun focusRequester() = focusRequester

    fun listState() = listState

    fun items(): List<Pair<String, TextFieldState>> = items

    fun start() {
        items.clear()
        setting.items()
            .map { it.key.toString() to TextFieldState(it.value.toString()) }
            .forEach(items::add)
    }

    // TODO Delete it
    fun update(key: String) {
        val index = items.indexOfFirst { it.first === key }
        if (index == -1) {
            return
        }
    }

    fun openFile() {
        val logFilePath = Path.of("user/setting.properties")
        viewModel.openFile(logFilePath)
    }

    fun save() {
        items.filter {
            it.second.text.isNotBlank()
        }.forEach {
            setting.update(it.first, it.second.text.toString())
        }
        setting.save()
    }

}