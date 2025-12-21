package jp.toastkid.yobidashi4.presentation.editor.markdown.text

class TableFormConverter {

    operator fun invoke(text: String): String {
        val putEndLineBreak = if (text.endsWith("\n")) "\n" else ""

        val lines = text.split("\n")
        return lines.mapNotNull { line ->
            val tuple = line.split(" ")
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty()) {
                return@mapNotNull null
            }

            if (tuple.size != 2 || !(tuple[0].contains("時") && tuple[0].contains("分"))) {
                return@mapNotNull "| ${trimmedLine.replace(" ", " | ")}"
            }
            val fill = with(StringBuilder()) {
                repeat(6 - tuple[0].length) { append(" ") }
                toString()
            }
            return@mapNotNull "| ${trimmedLine.replace(" ", "$fill | ")}"
        }.joinToString("\n") + putEndLineBreak
    }

}