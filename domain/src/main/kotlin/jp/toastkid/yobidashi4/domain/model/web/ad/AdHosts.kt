package jp.toastkid.yobidashi4.domain.model.web.ad

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.stream.Collectors

class AdHosts(private val adHosts: Set<String>) {

    fun contains(url: String?): Boolean {
        return url != null && adHosts.any(url::contains)
    }

    companion object {

        fun make(): AdHosts {
            val loadInputStream = loadInputStream() ?: return AdHosts(emptySet())

            val adHosts = BufferedReader(InputStreamReader(loadInputStream)).use { reader ->
                reader.lines().collect(Collectors.toUnmodifiableSet())
            }

            return AdHosts(adHosts)
        }

        fun loadInputStream(): InputStream? =
            AdHosts::class.java.classLoader.getResourceAsStream("web/ad_hosts.txt")

    }

}