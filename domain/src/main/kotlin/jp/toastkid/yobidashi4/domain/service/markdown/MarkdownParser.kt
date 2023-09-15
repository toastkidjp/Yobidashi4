package jp.toastkid.yobidashi4.domain.service.markdown

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.markdown.HorizontalRule
import jp.toastkid.yobidashi4.domain.model.markdown.ListLineBuilder
import jp.toastkid.yobidashi4.domain.model.markdown.Markdown
import jp.toastkid.yobidashi4.domain.model.markdown.TextBlock
import jp.toastkid.yobidashi4.domain.model.slideshow.Slide
import jp.toastkid.yobidashi4.domain.service.slideshow.BackgroundExtractor
import jp.toastkid.yobidashi4.domain.service.slideshow.CodeBlockBuilder
import jp.toastkid.yobidashi4.domain.service.slideshow.ImageExtractor
import jp.toastkid.yobidashi4.domain.service.slideshow.TableBuilder
import kotlin.io.path.nameWithoutExtension

class MarkdownParser {

    /** Slide builder.  */
    private var builder: Slide? = Slide()

    /** Table builder.  */
    private var tableBuilder: TableBuilder? = null

    private var codeBlockBuilder = CodeBlockBuilder()

    private val imageExtractor = ImageExtractor()

    private val backgroundExtractor = BackgroundExtractor()

    private val stringBuilder = StringBuilder()

    private val listLineBuilder = ListLineBuilder()

    private val orderedListPrefixPattern = "^[0-9]+\\.".toRegex()

    private val horizontalRulePattern = "^-{3,}$".toRegex()

    /**
     * Convert to Slides.
     * @return List&lt;Slide&gt;
     */
    operator fun invoke(path: Path): Markdown {
        val markdown = Markdown(title = path.nameWithoutExtension)
        try {
            Files.lines(path).forEach { line ->
                if (line.startsWith("#")) {
                    markdown.add(
                        TextBlock(
                            line.substring(line.indexOf(" ") + 1),
                            level = line.split(" ")[0].length
                        )
                    )
                    return@forEach
                }

                if (line.startsWith("![")) {
                    markdown.addAll(imageExtractor.invoke(line))
                    return@forEach
                }

                if (line.startsWith("> ")) {
                    markdown.add(TextBlock(line, quote = true))
                    return@forEach
                }
                // Adding code block.
                if (line.startsWith("```")) {
                    if (codeBlockBuilder.inCodeBlock()) {
                        codeBlockBuilder.build().let {
                            markdown.add(it)
                            codeBlockBuilder.initialize()
                        }
                        return@forEach
                    }

                    codeBlockBuilder.startCodeBlock()
                    val index = line.indexOf(":")
                    val lastIndex = if (index == -1) line.length else index
                    codeBlockBuilder.setCodeFormat(line.substring(3, lastIndex))
                    return@forEach
                }
                if (codeBlockBuilder.shouldAppend(line)) {
                    codeBlockBuilder.append(line)
                    return@forEach
                }

                if (TableBuilder.isTableStart(line)) {
                    if (tableBuilder == null) {
                        tableBuilder = TableBuilder()
                    }

                    if (TableBuilder.shouldIgnoreLine(line)) {
                        return@forEach
                    }

                    if (tableBuilder?.hasColumns() == false) {
                        tableBuilder?.setColumns(line)
                        return@forEach
                    }

                    tableBuilder?.addTableLines(line)
                    return@forEach
                }

                if (tableBuilder != null) {
                    tableBuilder?.build()?.let {
                        markdown.add(it)
                    }
                    tableBuilder = null
                }

                if (line.startsWith("- [ ] ") || line.startsWith("- [x] ")) {
                    listLineBuilder.setTaskList()
                    listLineBuilder.add(line)
                    return@forEach
                }

                if (line.startsWith("- ")) {
                    listLineBuilder.add(line)
                    return@forEach
                }

                if (orderedListPrefixPattern.containsMatchIn(line)) {
                    listLineBuilder.setOrdered()
                    listLineBuilder.add(line)
                    return@forEach
                }

                if (horizontalRulePattern.containsMatchIn(line)) {
                    markdown.add(HorizontalRule())
                    return@forEach
                }

                if (listLineBuilder.isNotEmpty()) {
                    markdown.add(listLineBuilder.build())
                    listLineBuilder.clear()
                    return@forEach
                }

                // Not code.
                if (line.isNotEmpty()) {
                    stringBuilder.append(line)
                    return@forEach
                }

                if (stringBuilder.isNotEmpty()) {
                    markdown.add(TextBlock(stringBuilder.toString()))
                    stringBuilder.setLength(0)
                    return@forEach
                }
            }

            if (stringBuilder.isNotEmpty()) {
                markdown.add(TextBlock(stringBuilder.toString()))
            }
            if (listLineBuilder.isNotEmpty()) {
                markdown.add(listLineBuilder.build())
                listLineBuilder.clear()
            }
            if (tableBuilder != null) {
                tableBuilder?.build()?.let {
                    markdown.add(it)
                }
                tableBuilder = null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return markdown
    }

}