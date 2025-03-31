package jp.toastkid.yobidashi4.presentation.setting

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.input.TextFieldValue
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

    private val items = mutableStateListOf<Pair<String, TextFieldValue>>()

    fun onKeyEvent(coroutineScope: CoroutineScope, keyEvent: KeyEvent): Boolean {
        return true
    }

    fun focusRequester() = focusRequester

    fun listState() = listState

    fun items() = items

    fun start() {
        items.clear()
        setting.items().forEach { entry ->
            items.add(entry.key.toString() to TextFieldValue(entry.value.toString()))
        }
    }

    fun update(key: String, it: TextFieldValue) {
        val index = items.indexOfFirst { it.first === key }
        if (index == -1) {
            return
        }
        items.set(index, key to it)
    }

    fun openFile() {
        val logFilePath = Path.of("user/setting.properties")
        viewModel.openFile(logFilePath)
    }

    fun save() {
        items.filter {
            it.second.text.isNotBlank()
        }.forEach {
            setting.update(it.first, it.second.text)
        }
        setting.save()
    }

}