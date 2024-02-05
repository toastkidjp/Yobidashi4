package jp.toastkid.yobidashi4.domain.service.web

import java.net.URI
import java.util.Formatter

class SiteSearchUrlGenerator {

    operator fun invoke(rawQuery: String, currentSiteUrl: String) =
        Formatter().format("https://www.google.com/search?as_dt=i&as_sitesearch=%s&as_q=%s", URI(currentSiteUrl).host, rawQuery).toString()

}