package jp.toastkid.yobidashi4.presentation.editor.legacy.popup

import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import jp.toastkid.yobidashi4.presentation.editor.legacy.MenuCommand
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.channels.Channel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PopupMenuInitializer(private val popupMenu: JPopupMenu, private val channel: Channel<MenuCommand>) {

    operator fun invoke() {
        val viewModel = object : KoinComponent { val viewModel: MainViewModel by inject() }.viewModel
        val toTableMenu = JMenuItem("To table")
        toTableMenu.addActionListener {
            viewModel.emitEditorCommand(MenuCommand.TO_TABLE)
        }
        popupMenu.add(toTableMenu)

        val blockQuotationMenu = JMenuItem("Block quote").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.BLOCKQUOTE)
            }
        }
        popupMenu.add(blockQuotationMenu)

        val hyphenListMenu = JMenuItem("Unordered list").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.UNORDERED_LIST)
            }
        }
        popupMenu.add(hyphenListMenu)

        val numberedListMenu = JMenuItem()
        numberedListMenu.action = object : AbstractAction("Ordered list") {
            override fun actionPerformed(e: ActionEvent?) {
                viewModel.emitEditorCommand(MenuCommand.ORDERED_LIST)
            }
        }
        popupMenu.add(numberedListMenu)

        val taskListMenu = JMenuItem("Task list").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.TASK_LIST)
            }
        }
        popupMenu.add(taskListMenu)

        val boldMenu = JMenuItem("Bold").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.BOLD)
            }
        }
        popupMenu.add(boldMenu)

        val italicMenu = JMenuItem("Italic").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.ITALIC)
            }
        }
        popupMenu.add(italicMenu)

        val strikethroughMenu = JMenuItem("Strikethrough").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.STRIKETHROUGH)
            }
        }
        popupMenu.add(strikethroughMenu)

        val codeBlockMenu = JMenuItem("Code block")
        codeBlockMenu.addActionListener {
            viewModel.emitEditorCommand(MenuCommand.CODE_BLOCK)
        }
        popupMenu.add(codeBlockMenu)

        popupMenu.add(JMenuItem("Trimming").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.TRIMMING)
            }
        })

        val fontColorMenu = JMenuItem("Font color").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.FONT_COLOR)
            }
        }
        popupMenu.add(fontColorMenu)

        popupMenu.add(
            JMenuItem("Paste as markdown link").also {
                it.addActionListener {
                    viewModel.emitEditorCommand(MenuCommand.PASTE_LINK_WITH_TITLE)
                }
            }
        )

        popupMenu.add(
                JMenuItem("To hyperlink").also {
                    it.addActionListener {
                        viewModel.emitEditorCommand(MenuCommand.TO_HYPERLINK)
                    }
                }
        )

        popupMenu.add(
            JMenuItem("URL Encode").also {
                it.addActionListener {
                    viewModel.emitEditorCommand(MenuCommand.URL_ENCODE)
                }
            }
        )

        popupMenu.add(
            JMenuItem("URL Decode").also {
                it.addActionListener {
                    viewModel.emitEditorCommand(MenuCommand.URL_DECODE)
                }
            }
        )

        val countMenu = JMenuItem("Count").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.COUNT)
            }
        }
        popupMenu.add(countMenu)

        val webSearchMenu = JMenuItem("Web search").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.WEB_SEARCH)
            }
        }
        popupMenu.add(webSearchMenu)

        val openUrlMenu = JMenuItem("Open URL").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.OPEN_URL)
            }
        }
        popupMenu.add(openUrlMenu)

        val dictionaryMenu = JMenuItem("Dictionary").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.DICTIONARY_SEARCH)
            }
        }
        popupMenu.add(dictionaryMenu)

        val translateMenu = JMenuItem("Translate to English").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.TRANSLATION_TO_ENGLISH)
            }
        }
        popupMenu.add(translateMenu)

        val horizontalRuleMenu = JMenuItem("Horizontal rule").also {
            it.addActionListener {
                viewModel.emitEditorCommand(MenuCommand.HORIZONTAL_RULE)
            }
        }
        popupMenu.add(horizontalRuleMenu)
    }
}