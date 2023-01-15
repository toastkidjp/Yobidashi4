package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.nio.file.Path
import kotlin.io.path.name
import kotlinx.coroutines.CoroutineScope

class Editor(
    val fileName: String,
    val lines: (backgroundScope: CoroutineScope) -> Lines,
) {
    var close: (() -> Unit)? = null
    lateinit var selection: SingleSelection

    val isActive: Boolean
        get() = selection.selected === this

    fun activate() {
        selection.selected = this
    }

    class Line(val number: Int, val content: Content)

    interface Lines {
        val lineNumberDigitCount: Int get() = size.toString().length
        val size: Int
        operator fun get(index: Int): Line
    }

    class Content(val value: MutableState<String>, val isCode: Boolean)
}

fun Editor(file: Path) = Editor(
    fileName = file.name
) { backgroundScope ->
    val textLines = try {
        TextLines.loadLines(file, backgroundScope)
    } catch (e: Throwable) {
        e.printStackTrace()
        TextLines.empty()
    }
    val isCode = file.name.endsWith(".kt", ignoreCase = true)

    fun content(index: Int): Editor.Content {
        val text = textLines.get(index)
        val state = mutableStateOf(text)
        return Editor.Content(state, isCode)
    }

    object : Editor.Lines {
        override val size get() = textLines.size

        override fun get(index: Int) = Editor.Line(
            number = index + 1,
            content = content(index)
        )
    }
}
