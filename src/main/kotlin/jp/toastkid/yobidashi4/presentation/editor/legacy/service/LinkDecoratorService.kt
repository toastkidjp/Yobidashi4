package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern
import kotlin.streams.asSequence

class LinkDecoratorService {

    operator fun invoke(link: String): String {
        val url = try {
            URL(link)
        } catch (e: MalformedURLException) {
            return link
        }

        BufferedReader(InputStreamReader(url.openConnection().inputStream)).use { reader ->
            val content = reader.lines()
                    .asSequence()
                    .firstOrNull { it.contains("<title") } ?: return link

            val matcher = PATTERN.matcher(content)
            return if (matcher.find()) "[${matcher.group(1)}]($link)" else link
        }
    }

    companion object {

        private val PATTERN = Pattern.compile(
                "<title.*>(.+?)</title>",
                Pattern.DOTALL or Pattern.CASE_INSENSITIVE
        )

    }

}