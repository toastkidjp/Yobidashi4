package jp.toastkid.yobidashi4.domain.service.slideshow

import java.util.regex.Matcher
import java.util.regex.Pattern
import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine

class ImageExtractor {

    operator fun invoke(line: String?): List<ImageLine> {
        if (line.isNullOrBlank()) {
            return emptyList()
        }

        return extractImageUrls(line)
            .filterNotNull()
            .map {
                ImageLine(it)
            }
    }

    /**
     * Extract image url from text.
     * @param line line
     * @return image url
     */
    private fun extractImageUrls(line: String): List<String?> {
        val imageUrls: MutableList<String?> = ArrayList()
        val matcher: Matcher = IMAGE.matcher(line)
        while (matcher.find()) {
            imageUrls.add(matcher.group(2))
        }
        return imageUrls
    }

    companion object {

        /** In-line image pattern.  */
        private val IMAGE: Pattern = Pattern.compile("\\!\\[(.+?)\\]\\((.+?)\\)")

    }

}