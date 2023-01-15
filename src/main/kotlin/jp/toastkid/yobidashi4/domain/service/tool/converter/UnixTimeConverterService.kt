package jp.toastkid.yobidashi4.domain.service.tool.converter

import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField

class UnixTimeConverterService {

    operator fun invoke() {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)
        val unixTimeInput = JTextField()
        val offset = OffsetDateTime.now().offset
        unixTimeInput.text = LocalDateTime.now().toInstant(offset).toEpochMilli().toString()
        val dateTime = JTextField()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        dateTime.text = LocalDateTime.now().format(dateFormatter).toString()

        unixTimeInput.preferredSize = Dimension(100, 24)
        unixTimeInput.addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                // unixtime -> datetime
                dateTime.text = LocalDateTime
                    .ofInstant(Instant.ofEpochMilli(unixTimeInput.text.toLong()), ZoneId.systemDefault())
                    .format(dateFormatter)
            }
        })
        panel.add(JLabel("Please would you input UNIX TIME."))
        panel.add(JLabel("UNIX TIME"))
        panel.add(unixTimeInput)
        panel.add(JLabel("Date time"))
        dateTime.preferredSize = Dimension(100, 24)
        dateTime.addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent?) {
                // datetime -> unixtime
                try {
                    unixTimeInput.text = LocalDateTime.parse(dateTime.text, dateFormatter)
                        .toInstant(offset)
                        .toEpochMilli()
                        .toString()
                } catch (e: DateTimeException) {
                    // > /dev/null
                }
            }
        })
        panel.add(dateTime)
        JOptionPane.showMessageDialog(null, panel, "UnixTime converter", JOptionPane.QUESTION_MESSAGE)
    }

}