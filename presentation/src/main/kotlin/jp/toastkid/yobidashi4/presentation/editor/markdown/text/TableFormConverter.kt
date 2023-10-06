package jp.toastkid.yobidashi4.presentation.editor.markdown.text

class TableFormConverter {

    operator fun invoke(text: String) =
            "| ${text.trim().replace(" ", " | ").replace("\n", "\n| ")}\n"

}