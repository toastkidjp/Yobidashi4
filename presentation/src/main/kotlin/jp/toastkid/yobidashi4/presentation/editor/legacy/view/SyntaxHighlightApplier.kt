package jp.toastkid.yobidashi4.presentation.editor.legacy.view

import java.awt.Color
import java.awt.Font
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.Style
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rsyntaxtextarea.SyntaxScheme
import org.fife.ui.rsyntaxtextarea.Token
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory

class SyntaxHighlightApplier {

    operator fun invoke(editorArea: RSyntaxTextArea, extension: String) {
        when (extension) {
            "md", "txt" -> {
                setCustomMarkdownStyle(editorArea)
            }
            "java" -> {
                editorArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_JAVA
            }
            "kt" -> {
                editorArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_KOTLIN
            }
            "py" -> editorArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_PYTHON
            "js", "tsx" -> editorArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT
            "yaml" -> editorArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_YAML
            "json" -> editorArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_JSON
            "html" -> editorArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_HTML
            "xml" -> editorArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_XML
            "sh" -> editorArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL
            else ->  editorArea.syntaxScheme = SyntaxScheme.loadFromString(extension)
        }
    }

    private fun setCustomMarkdownStyle(editorArea: RSyntaxTextArea) {
        (TokenMakerFactory.getDefaultInstance() as? AbstractTokenMakerFactory)
            ?.putMapping(customStyle, MarkdownTokenMaker::class.java.canonicalName)
        editorArea.syntaxEditingStyle = customStyle
        val boldFont = editorArea.font.deriveFont(Font.BOLD)
        val syntaxScheme = editorArea.syntaxScheme
        syntaxScheme.setStyle(
            Token.COMMENT_EOL,
            Style(Color(0, 128, 0), null, boldFont)
        )
        syntaxScheme.setStyle(
            Token.LITERAL_NUMBER_HEXADECIMAL,
            Style(Color(128, 0, 220), null, boldFont)
        )
    }

}

private val customStyle = "text/plain2"