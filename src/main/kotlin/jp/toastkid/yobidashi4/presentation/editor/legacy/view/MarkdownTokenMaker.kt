package jp.toastkid.yobidashi4.presentation.editor.legacy.view

import javax.swing.text.Segment
import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities
import org.fife.ui.rsyntaxtextarea.Token
import org.fife.ui.rsyntaxtextarea.TokenMap

class MarkdownTokenMaker : AbstractTokenMaker() {

    private var currentTokenStart = -1

    private var currentTokenType = -1

    /**
     * Returns a list of tokens representing the given text.
     *
     * @param text The text to break into tokens.
     * @param startTokenType The token with which to start tokenizing.
     * @param startOffset The offset at which the line of tokens begins.
     * @return A linked list of tokens representing `text`.
     */
    override fun getTokenList(text: Segment?, startTokenType: Int, startOffset: Int): Token? {
        resetTokenList()
        text ?: return null
        val array = text.array
        val offset = text.offset
        val count = text.count
        val end = offset + count

        // Token starting offsets are always of the form:
        // 'startOffset + (currentTokenStart-offset)', but since startOffset and
        // offset are constant, tokens' starting positions become:
        // 'newStartOffset+currentTokenStart'.
        val newStartOffset = startOffset - offset
        currentTokenStart = offset
        currentTokenType = startTokenType
        var i = offset
        while (i < end) {
            val c = array[i]
            when (currentTokenType) {
                Token.NULL -> {
                    currentTokenStart = i // Starting a new token here.
                    when (c) {
                        ' ', '\t' -> currentTokenType = Token.WHITESPACE
                        '"' -> currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE
                        '#' -> currentTokenType = Token.COMMENT_EOL
                        '>' -> currentTokenType = Token.COMMENT_MULTILINE
                        '|' -> currentTokenType = Token.LITERAL_NUMBER_HEXADECIMAL
                        '-' -> currentTokenType = Token.DATA_TYPE
                        else -> {
                            if (RSyntaxUtilities.isLetter(c) || c == '/' || c == '_') {
                                currentTokenType = Token.IDENTIFIER
                                break
                            }

                            // Anything not currently handled - mark as an identifier
                            currentTokenType = Token.IDENTIFIER
                        }
                    }
                }
                Token.WHITESPACE -> when (c) {
                    ' ', '\t' -> {}
                    '"' -> {
                        addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart)
                        currentTokenStart = i
                        currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE
                    }
                    '#' -> {
                        addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart)
                        currentTokenStart = i
                        currentTokenType = Token.COMMENT_EOL
                    }
                    else -> {
                        addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart)
                        currentTokenStart = i
                        if (RSyntaxUtilities.isDigit(c)) {
                            currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT
                            break
                        } else if (RSyntaxUtilities.isLetter(c) || c == '/' || c == '_') {
                            currentTokenType = Token.IDENTIFIER
                            break
                        }

                        // Anything not currently handled - mark as identifier
                        currentTokenType = Token.IDENTIFIER
                    }
                }
                Token.IDENTIFIER -> when (c) {
                    ' ', '\t' -> {
                        addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart)
                        currentTokenStart = i
                        currentTokenType = Token.WHITESPACE
                    }
                    '"' -> {
                        addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart)
                        currentTokenStart = i
                        currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE
                    }
                    else -> if (RSyntaxUtilities.isLetterOrDigit(c) || c == '/' || c == '_') {
                        break // Still an identifier of some type.
                    }
                }
                Token.COMMENT_EOL, Token.COMMENT_MULTILINE, Token.LITERAL_NUMBER_HEXADECIMAL, Token.DATA_TYPE -> {
                    i = end - 1
                    addToken(text, currentTokenStart, i, currentTokenType, newStartOffset + currentTokenStart)
                    // We need to set token type to null so at the bottom we don't add one more token.
                    currentTokenType = Token.NULL
                }
                Token.LITERAL_STRING_DOUBLE_QUOTE -> if (c == '"') {
                    addToken(
                        text,
                        currentTokenStart,
                        i,
                        Token.LITERAL_STRING_DOUBLE_QUOTE,
                        newStartOffset + currentTokenStart
                    )
                    currentTokenType = Token.NULL
                }
                else -> when (c) {
                    ' ', '\t' -> {
                        addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart)
                        currentTokenStart = i
                        currentTokenType = Token.WHITESPACE
                    }
                    '"' -> {
                        addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart)
                        currentTokenStart = i
                        currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE
                    }
                    else -> if (RSyntaxUtilities.isLetterOrDigit(c) || c == '/' || c == '_') {
                        break
                    }
                }
            }
            i++
        }
        when (currentTokenType) {
            Token.LITERAL_STRING_DOUBLE_QUOTE -> addToken(
                text,
                currentTokenStart,
                end - 1,
                currentTokenType,
                newStartOffset + currentTokenStart
            )
            Token.NULL -> addNullToken()
            else -> {
                addToken(text, currentTokenStart, end - 1, currentTokenType, newStartOffset + currentTokenStart)
                addNullToken()
            }
        }

        // Return the first token in our linked list.
        return firstToken
    }

    override fun getWordsToHighlight(): TokenMap {
        return TokenMap()
    }

}