package jp.toastkid.yobidashi4.infrastructure.repository.factory

import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class HttpUrlConnectionFactory {

    operator fun invoke(url: URL): HttpURLConnection? =
        url.openConnection() as? HttpsURLConnection

}