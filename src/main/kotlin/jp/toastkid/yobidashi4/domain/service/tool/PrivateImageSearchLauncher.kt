package jp.toastkid.yobidashi4.domain.service.tool

import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory

class PrivateImageSearchLauncher : KoinComponent {

    private val setting: Setting by inject()

    operator fun invoke(imageUrl: String) {
        try {
            Runtime.getRuntime().exec(
                arrayOf(
                    setting.privateSearchPath(),
                    setting.privateSearchOption(),
                    "https://www.bing.com/images/search?view=detailv2&iss=sbi&q=imgurl:${URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.name())}"
                )
            )
        } catch (e: IOException) {
            LoggerFactory.getLogger(javaClass).warn("Runtime error.", e)
        }
    }

}