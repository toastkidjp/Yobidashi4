package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.MultiParagraph
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import jp.toastkid.yobidashi4.presentation.text.code.CodeStringBuilder
import kotlin.math.min

class CodeBlockViewModel {

    private val verticalScrollState = ScrollState(0)

    private val horizontalScrollState = ScrollState(0)

    private val content = TextFieldState()

    private val codeStringBuilder = CodeStringBuilder()

    private val lineCountState = mutableStateOf(1)

    fun maxHeight(fontSize: TextUnit) = min(lineCountState.value * fontSize.value * 1.55.em.value, 800f).dp

    fun content() = content

    @OptIn(ExperimentalFoundationApi::class)
    fun verticalScrollState() = verticalScrollState

    fun horizontalScrollState() = horizontalScrollState

    fun outputTransformation() = codeStringBuilder

    fun lineNumberTexts(): List<String> {
        val max = lineCountState.value
        val length = max.toString().length

        return (0 until max).map {
            val lineNumberCount = it + 1
            val fillCount = length - lineNumberCount.toString().length
            with(StringBuilder()) {
                repeat(fillCount) {
                    append(" ")
                }
                append(lineNumberCount)
            }.toString()
        }
    }

    fun setMultiParagraph(multiParagraph: MultiParagraph) {
        val lineCount = multiParagraph.lineCount
        lineCountState.value = lineCount
    }

    fun start(code: String) {
        content.setTextAndPlaceCursorAtEnd(code)
    }

}