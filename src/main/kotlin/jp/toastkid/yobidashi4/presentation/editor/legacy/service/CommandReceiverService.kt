package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.awt.Desktop
import java.io.IOException
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import jp.toastkid.yobidashi4.presentation.editor.legacy.MenuCommand
import jp.toastkid.yobidashi4.presentation.editor.legacy.text.BlockQuotation
import jp.toastkid.yobidashi4.presentation.editor.legacy.text.ListHeadAdder
import jp.toastkid.yobidashi4.presentation.editor.legacy.text.NumberedListHeadAdder
import jp.toastkid.yobidashi4.presentation.editor.legacy.text.TableFormConverter
import jp.toastkid.yobidashi4.presentation.editor.legacy.text.TrimmingService
import jp.toastkid.yobidashi4.presentation.editor.legacy.view.EditorAreaView
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory

class CommandReceiverService(
    private val editorAreaView: EditorAreaView
) : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val setting: Setting by inject()

    suspend operator fun invoke() {
        viewModel.editorCommandFlow().collect { command ->
            when (command) {
                MenuCommand.SAVE -> {
                    val path = (viewModel.currentTab() as? EditorTab)?.path ?: return@collect
                    try {
                        withContext(Dispatchers.IO) {
                            val textArray = editorAreaView.getTextArray()
                            if (textArray.isNotEmpty()) {
                                Files.write(path, textArray)
                            }
                            viewModel.updateEditorContent(path, editorAreaView.getText(), -1, true)
                        }
                    } catch (e: IOException) {
                        LoggerFactory.getLogger(javaClass).warn("Storing error.", e)
                    }
                }
                MenuCommand.PASTE_AS_QUOTATION -> {
                    val text = withContext(Dispatchers.IO) { ClipboardFetcher().invoke() } ?: return@collect
                    val quotedText = BlockQuotation().invoke(text) ?: return@collect
                    editorAreaView.insertText(quotedText)
                }
                MenuCommand.PASTE_LINK_WITH_TITLE -> {
                    val text = withContext(Dispatchers.IO) { ClipboardFetcher().invoke() } ?: return@collect
                    val decorated = LinkDecoratorService().invoke(text)
                    editorAreaView.insertText(decorated)
                }
                MenuCommand.TO_TABLE -> {
                    editorAreaView.replaceSelected { text ->
                        TableFormConverter().invoke(text)
                    }
                }
                MenuCommand.UNORDERED_LIST -> {
                    editorAreaView.replaceSelected { text ->
                        ListHeadAdder().invoke(text, "-") ?: text
                    }
                }
                MenuCommand.ORDERED_LIST -> {
                    editorAreaView.replaceSelected { text ->
                        NumberedListHeadAdder().invoke(text) ?: text
                    }
                }
                MenuCommand.TASK_LIST -> {
                    editorAreaView.replaceSelected { text ->
                        ListHeadAdder().invoke(text, "- [ ]") ?: text
                    }
                }
                MenuCommand.BLOCKQUOTE -> {
                    editorAreaView.replaceSelected { text ->
                        BlockQuotation().invoke(text) ?: text
                    }
                }
                MenuCommand.TRIMMING -> {
                    editorAreaView.replaceSelected { text ->
                        TrimmingService().invoke(text) ?: text
                    }
                }
                MenuCommand.CODE_BLOCK -> editorAreaView.replaceSelected { "```\n$it```" }
                MenuCommand.ITALIC -> editorAreaView.replaceSelected { "*$it*" }
                MenuCommand.BOLD -> editorAreaView.replaceSelected { "**$it**" }
                MenuCommand.STRIKETHROUGH -> editorAreaView.replaceSelected { "~~$it~~" }
                MenuCommand.SURROUND_JA_BRACKET -> editorAreaView.replaceSelected { "「$it」" }
                MenuCommand.SURROUND_BRACKET -> editorAreaView.replaceSelected { "($it)" }
                MenuCommand.FONT_COLOR -> {
                    val color = ColorChooserService().invoke() ?: return@collect
                    editorAreaView.replaceSelected { text ->
                        "<font color='#${Integer.toHexString(color.rgb)}'>$text</font>"
                    }
                }
                MenuCommand.TO_HYPERLINK -> {
                    editorAreaView.replaceSelected { text ->
                        LinkDecoratorService().invoke(text)
                    }
                }
                MenuCommand.HORIZONTAL_RULE -> {
                    editorAreaView.insertText("---")
                }
                MenuCommand.REVERSE_CASE -> {
                    editorAreaView.replaceSelected(true) {
                        if (it.isEmpty()) return@replaceSelected it
                        return@replaceSelected if (it.toCharArray()[0].isUpperCase()) it.lowercase() else it.uppercase()
                    }
                }
                MenuCommand.URL_ENCODE -> {
                    editorAreaView.replaceSelected(true) {
                        if (it.isEmpty()) return@replaceSelected it
                        return@replaceSelected URLEncoder.encode(it, StandardCharsets.UTF_8)
                    }
                }
                MenuCommand.URL_DECODE -> {
                    editorAreaView.replaceSelected(true) {
                        if (it.isEmpty()) return@replaceSelected it
                        return@replaceSelected URLDecoder.decode(it, StandardCharsets.UTF_8)
                    }
                }
                MenuCommand.DUPLICATE_LINE -> {
                    editorAreaView.duplicateLine()
                }
                MenuCommand.EXTRACT_LINE -> {
                    editorAreaView.extractLine()
                }
                MenuCommand.SWITCH_EDITABLE -> {
                    editorAreaView.switchEditable()
                }
                MenuCommand.COUNT -> {
                    SwingUtilities.invokeLater {
                        JOptionPane.showMessageDialog(
                            null,
                            "Count: ${editorAreaView.count()}"
                        )
                    }
                }
                MenuCommand.WEB_SEARCH -> {
                    val selectedText = editorAreaView.selectedText()
                    if (selectedText.isBlank()) {
                        return@collect
                    }
                    if (selectedText.startsWith("http://") || selectedText.startsWith("https://")) {
                        viewModel.openUrl(selectedText, false)
                        return@collect
                    }
                    viewModel.openUrl("https://search.yahoo.co.jp/search?p=${encodeUtf8(selectedText)}", false)
                }
                MenuCommand.OPEN_URL -> {
                    val selectedText = editorAreaView.selectedText()
                    if (selectedText.isBlank() || selectedText.startsWith("https://").not()) {
                        return@collect
                    }
                    viewModel.openUrl(selectedText, false)
                }
                MenuCommand.OPEN_WITH_BROWSER -> {
                    val selectedText = editorAreaView.selectedText()
                    if (selectedText.isBlank() || selectedText.startsWith("https://").not()) {
                        return@collect
                    }
                    Desktop.getDesktop().browse(URI(selectedText))
                }
                MenuCommand.DICTIONARY_SEARCH -> {
                    val selectedText = editorAreaView.selectedText()
                    if (selectedText.isBlank()) {
                        return@collect
                    }
                    viewModel.openUrl("https://ejje.weblio.jp/content/${encodeUtf8(selectedText)}", false)
                }
                MenuCommand.TRANSLATION_TO_ENGLISH -> {
                    val selectedText = editorAreaView.selectedText()
                    if (selectedText.isBlank()) {
                        return@collect
                    }
                    viewModel.openUrl("https://translate.google.co.jp/?hl=en&sl=auto&tl=en&text=${encodeUtf8(selectedText)}&op=translate", false)
                }
                MenuCommand.SWITCH_WRAP_LINE -> {
                    setting.switchWrapLine()
                    // Avoiding for java.lang.NullPointerException: Cannot load from char array because "this.text" is null.
                    try {
                        editorAreaView.refresh()
                    } catch (e: RuntimeException) {
                        LoggerFactory.getLogger(javaClass).warn("wrap error", e)
                    }
                }
                MenuCommand.EDITOR_TO_TOP -> {
                    editorAreaView.toTop()
                }
                MenuCommand.EDITOR_TO_BOTTOM-> {
                    editorAreaView.toBottom()
                }
                MenuCommand.REFRESH -> {
                    editorAreaView.refresh()
                }
            }
        }
    }

    private fun encodeUtf8(selectedText: String) = URLEncoder.encode(selectedText, StandardCharsets.UTF_8.name())
}