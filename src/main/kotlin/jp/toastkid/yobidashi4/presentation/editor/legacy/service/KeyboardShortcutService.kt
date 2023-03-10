package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.awt.event.KeyEvent
import jp.toastkid.yobidashi4.presentation.editor.legacy.MenuCommand
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KeyboardShortcutService {

    operator fun invoke(e: KeyEvent) {
        if (e.isControlDown.not()) {
            return
        }

        val viewModel = object : KoinComponent { val viewModel: MainViewModel by inject() }.viewModel
        if (e.isShiftDown && e.keyCode == KeyEvent.VK_UP) {
            CoroutineScope(Dispatchers.Default).launch {
                viewModel.emitEditorCommand(MenuCommand.EDITOR_TO_TOP)
            }
        }

        if (e.isShiftDown && e.keyCode == KeyEvent.VK_DOWN) {
            CoroutineScope(Dispatchers.Default).launch {
                viewModel.emitEditorCommand(MenuCommand.EDITOR_TO_BOTTOM)
            }
        }

        if (e.isShiftDown && e.keyCode == KeyEvent.VK_U) {
            CoroutineScope(Dispatchers.Default).launch {
                viewModel.emitEditorCommand(MenuCommand.REVERSE_CASE)
            }
            return
        }

        if (e.isShiftDown && e.keyCode == KeyEvent.VK_O) {
            CoroutineScope(Dispatchers.Default).launch {
                viewModel.emitEditorCommand(MenuCommand.WEB_SEARCH)
            }
            return
        }

        if (e.isShiftDown && e.keyCode == KeyEvent.VK_W) {
            CoroutineScope(Dispatchers.Default).launch {
                viewModel.emitEditorCommand(MenuCommand.SWITCH_WRAP_LINE)
            }
            return
        }

        if (e.isShiftDown && e.keyCode == KeyEvent.VK_N) {
            CoroutineScope(Dispatchers.Default).launch {
                viewModel.emitEditorCommand(MenuCommand.SWITCH_EDITABLE)
            }
            return
        }

        if (e.isAltDown && e.keyCode == KeyEvent.VK_O) {
            CoroutineScope(Dispatchers.Default).launch {
                viewModel.emitEditorCommand(MenuCommand.OPEN_WITH_BROWSER)
            }
            return
        }

        CoroutineScope(Dispatchers.Default).launch {
            val command = when (e.keyCode) {
                KeyEvent.VK_T -> MenuCommand.TO_TABLE
                KeyEvent.VK_I -> MenuCommand.ITALIC
                KeyEvent.VK_B -> MenuCommand.BOLD
                KeyEvent.VK_Q -> MenuCommand.PASTE_AS_QUOTATION
                KeyEvent.VK_S -> MenuCommand.SAVE
                KeyEvent.VK_L -> MenuCommand.TO_HYPERLINK
                KeyEvent.VK_PERIOD -> MenuCommand.BLOCKQUOTE
                KeyEvent.VK_CIRCUMFLEX -> MenuCommand.STRIKETHROUGH
                KeyEvent.VK_1 -> MenuCommand.ORDERED_LIST
                KeyEvent.VK_2 -> MenuCommand.TASK_LIST
                KeyEvent.VK_MINUS -> MenuCommand.UNORDERED_LIST
                KeyEvent.VK_AT -> MenuCommand.CODE_BLOCK
                KeyEvent.VK_8 -> MenuCommand.SURROUND_BRACKET
                KeyEvent.VK_CLOSE_BRACKET -> MenuCommand.SURROUND_JA_BRACKET
                else -> null
            } ?: return@launch
            viewModel.emitEditorCommand(command)
        }
    }

}