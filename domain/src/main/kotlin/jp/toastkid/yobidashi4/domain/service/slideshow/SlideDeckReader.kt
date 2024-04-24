package jp.toastkid.yobidashi4.domain.service.slideshow

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Matcher
import java.util.regex.Pattern
import jp.toastkid.yobidashi4.domain.model.slideshow.Slide
import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck

class SlideDeckReader(private val pathToMarkdown: Path) {

    /** Slide builder.  */
    private var builder: Slide = Slide()

    /** Table builder.  */
    private val tableBuilder = TableBuilder()

    private var codeBlockBuilder = CodeBlockBuilder()

    private val imageExtractor = ImageExtractor()

    private val backgroundExtractor = BackgroundExtractor()

    /**
     * Convert to Slides.
     * @return List&lt;Slide&gt;
     */
    operator fun invoke(): SlideDeck {
        val deck = SlideDeck()
        try {
            Files.lines(pathToMarkdown).use { lines ->
                lines.forEach { line: String ->
                    if (line.startsWith("#")) {
                        if (builder.hasTitle()) {
                            builder.let {
                                deck.add(it)
                            }
                            builder = Slide()
                        }
                        if (line.startsWith("# ")) {
                            builder.setFront(true)
                        }
                        val titleText = line.substring(line.indexOf(" ")).trim { it <= ' ' }
                        builder.setTitle(titleText)
                        if (deck.title.isEmpty()) {
                            deck.title = titleText
                        }
                        return@forEach
                    }
                    if (line.startsWith("![")) {
                        if (BackgroundExtractor.shouldInvoke(line)) {
                            backgroundExtractor(line)?.let {
                                if (deck.background.isBlank()) {
                                    deck.background = it
                                    return@forEach
                                }
                                builder.setBackground(it)
                            }
                            return@forEach
                        }

                        builder.addLines(imageExtractor.invoke(line))
                        return@forEach
                    }
                    if (line.startsWith("[footer](")) {
                        val matcher: Matcher = FOOTER_TEXT.matcher(line)
                        if (matcher.find()) {
                            deck.footerText = matcher.group(1)
                        }
                        return@forEach
                    }
                    if (line.startsWith("> ")) {
                        builder.addQuotedLines(line)
                        return@forEach
                    }
                    // Adding code block.
                    if (line.startsWith("```")) {
                        if (codeBlockBuilder.inCodeBlock()) {
                            codeBlockBuilder.build().let {
                                builder.addLine(it)
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
                        if (!tableBuilder.active()) {
                            tableBuilder.setActive()
                        }

                        if (TableBuilder.shouldIgnoreLine(line)) {
                            return@forEach
                        }

                        if (tableBuilder.hasColumns() == false) {
                            tableBuilder.setColumns(line)
                            return@forEach
                        }

                        tableBuilder.addTableLines(line)
                        return@forEach
                    }

                    if (tableBuilder.active()) {
                        builder.addLine(tableBuilder.build())
                        tableBuilder.setInactive()
                        tableBuilder.clear()
                    }
                    // Not code.
                    if (line.isNotEmpty()) {
                        builder.addText(line)
                    }
                }
                builder.let {
                    deck.add(it)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return deck
    }

    companion object {

        /** In-line image pattern.  */
        private val FOOTER_TEXT: Pattern = Pattern.compile("\\[footer\\]\\((.+?)\\)")
    }
}