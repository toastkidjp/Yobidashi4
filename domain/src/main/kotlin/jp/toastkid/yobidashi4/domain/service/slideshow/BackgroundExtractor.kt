package jp.toastkid.yobidashi4.domain.service.slideshow

import java.util.regex.Matcher
import java.util.regex.Pattern

class BackgroundExtractor {

    /**
     * Extract background image url from text.
     * @param line line
     * @return image url
     */
    operator fun invoke(line: String): String? {
        val matcher: Matcher = BACKGROUND.matcher(line)
        return if (matcher.find()) matcher.group(1) else null
    }

    companion object {

        /** Background image pattern.  */
        private val BACKGROUND: Pattern = Pattern.compile("\\!\\[background\\]\\((.+?)\\)")

        fun shouldInvoke(line: String) = line.startsWith("![background](")
    }

}