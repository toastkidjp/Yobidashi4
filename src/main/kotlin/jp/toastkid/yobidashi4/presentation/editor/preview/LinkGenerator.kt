package jp.toastkid.yobidashi4.presentation.editor.preview

import java.util.regex.Pattern

/**
 * @author toastkidjp
 */
class LinkGenerator {

    operator fun invoke(text: String): String {
        return embedLinks(text, internalLinkPattern, 1)
    }

    private fun embedLinks(text: String, pattern: Pattern, group: Int): String {
        var converted = text
        var matcher = pattern.matcher(converted)
        while (matcher.find()) {
            converted = matcher.replaceFirst(InternalLinkScheme().makeLink(matcher.group(group)))
            matcher = pattern.matcher(converted)
        }
        return converted
    }

    companion object {

        private val internalLinkPattern =
            Pattern.compile("\\[\\[(.+?)\\]\\]", Pattern.DOTALL)

    }
}