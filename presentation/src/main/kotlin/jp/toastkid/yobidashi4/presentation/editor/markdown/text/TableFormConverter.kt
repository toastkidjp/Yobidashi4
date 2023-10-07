package jp.toastkid.yobidashi4.presentation.editor.markdown.text

class TableFormConverter {

    operator fun invoke(text: String): String {
        val putEndLineBreak = if (text.endsWith("\n")) "\n" else ""

        return "| ${text.trim().replace(" ", " | ").replace("\n", "\n| ")}$putEndLineBreak"
    }

}