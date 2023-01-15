package jp.toastkid.yobidashi4.domain.service.tool

import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory

class PrivateImageSearchService : KoinComponent {

    private val setting: Setting by inject()

    operator fun invoke() {
        val content = JPanel().also { panel ->
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            panel.add(JLabel(MESSAGE))
        }
        val input = JOptionPane.showInputDialog(content)
        try {
            URL(input)
        } catch (e: MalformedURLException) {
            null
        } ?: return

        try {
            Runtime.getRuntime().exec(
                arrayOf(
                    setting.privateSearchPath(),
                    setting.privateSearchOption(),
                    "https://www.bing.com/images/search?view=detailv2&iss=sbi&q=imgurl:${URLEncoder.encode(input, StandardCharsets.UTF_8.name())}"
                )
            )
        } catch (e: IOException) {
            LoggerFactory.getLogger(javaClass).warn("Runtime error.", e)
        }
    }

    companion object {
        private const val MESSAGE = "Please would you input search image URL?"
    }

}