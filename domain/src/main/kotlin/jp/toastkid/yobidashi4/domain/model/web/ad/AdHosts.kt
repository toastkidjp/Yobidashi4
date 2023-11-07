package jp.toastkid.yobidashi4.domain.model.web.ad

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

class AdHosts(private val adHosts: Set<String>) {

    fun contains(url: String?): Boolean {
        return adHosts.any { url?.contains(it) == true }
    }

    companion object {

        fun make() =
            AdHosts(
                AdHosts::class.java.classLoader.getResourceAsStream("web/ad_hosts.txt")?.use { stream ->
                    return@use BufferedReader(InputStreamReader(stream)).use { reader ->
                        reader.lines().collect(Collectors.toUnmodifiableSet())
                    }
                } ?: emptySet()
            )

    }

}