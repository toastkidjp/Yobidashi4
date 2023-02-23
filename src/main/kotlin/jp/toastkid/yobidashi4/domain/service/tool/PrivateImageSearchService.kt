package jp.toastkid.yobidashi4.domain.service.tool

import java.net.MalformedURLException
import java.net.URL
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import org.koin.core.component.KoinComponent

class PrivateImageSearchService : KoinComponent {

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

        PrivateImageSearchLauncher().invoke(input)
    }

    companion object {
        private const val MESSAGE = "Please would you input search image URL?"
    }

}