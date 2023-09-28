package jp.toastkid.yobidashi4.presentation.editor.legacy.finder

import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FinderAreaView(
    private val orderChannel: Channel<FindOrder>,
    messageChannel: Channel<String>
): KoinComponent {

    private val setting: Setting by inject()

    private val content = JPanel()

    init {
        content.layout = GridBagLayout()
        val constraints = GridBagConstraints()

        constraints.gridx = 0
        constraints.gridy = 0
        constraints.gridwidth = 1
        content.add(JLabel("Target"), constraints)

        val target = JTextField()
        target.preferredSize = Dimension(200, 36)
        constraints.gridx = 1
        constraints.gridy = 0
        constraints.gridwidth = 2
        content.add(target, constraints)

        constraints.gridx = 0
        constraints.gridy = 1
        constraints.gridwidth = 1
        content.add(JLabel("Replace"), constraints)

        val replace = JTextField()
        replace.preferredSize = Dimension(200, 36)
        constraints.gridx = 1
        constraints.gridy = 1
        constraints.gridwidth = 2
        content.add(replace, constraints)

        val caseCondition = JCheckBox("Case sensitive")
        caseCondition.addActionListener { setting.setUseCaseSensitiveInFinder(caseCondition.isSelected) }
        caseCondition.isSelected = setting.useCaseSensitiveInFinder()

        constraints.gridx = 3
        constraints.gridy = 0
        content.add(makeButtons(target, replace, caseCondition), constraints)

        constraints.gridx = 3
        constraints.gridy = 1
        content.add(caseCondition, constraints)

        val message = JLabel()
        message.preferredSize = Dimension(200, 36)
        message.font = message.font.deriveFont(14f)
        MessageReceiverService(messageChannel, message).invoke()
        constraints.gridx = 4
        constraints.gridy = 0
        content.add(message)

        constraints.gridx = 10
        content.add(JButton("x").also {
            it.addActionListener { content.isVisible = false }
        })

        content.isVisible = false
    }

    private fun makeButtons(target: JTextField, replace: JTextField, caseCondition: JCheckBox): JPanel {
        val buttons = JPanel()
        buttons.layout = BoxLayout(buttons, BoxLayout.X_AXIS)
        val upper = JButton()
        upper.preferredSize = Dimension(100, 36)
        upper.margin = Insets(10, 20, 10, 20)
        upper.action = object : AbstractAction("↑") {
            override fun actionPerformed(e: ActionEvent?) {
                if (target.text.isNullOrEmpty()) {
                    return
                }
                CoroutineScope(Dispatchers.Default).launch {
                    orderChannel.send(FindOrder(target.text, replace.text, true, caseSensitive = caseCondition.isSelected))
                }
            }
        }

        val downer = JButton()
        downer.margin = Insets(10, 20, 10, 20)
        downer.action = object : AbstractAction("↓") {
            override fun actionPerformed(e: ActionEvent?) {
                if (target.text.isNullOrEmpty()) {
                    return
                }
                CoroutineScope(Dispatchers.Default).launch {
                    orderChannel.send(FindOrder(target.text, replace.text, caseSensitive = caseCondition.isSelected))
                }
            }
        }

        val all = JButton()
        all.margin = Insets(10, 20, 10, 20)
        all.action = object : AbstractAction("All") {
            override fun actionPerformed(e: ActionEvent?) {
                if (target.text == replace.text) {
                    return
                }

                CoroutineScope(Dispatchers.Default).launch {
                    orderChannel.send(FindOrder(target.text, replace.text, invokeReplace = true, caseSensitive = caseCondition.isSelected))
                }
            }
        }

        buttons.add(upper)
        buttons.add(downer)
        buttons.add(all)
        return buttons
    }

    fun view(): JComponent = content

    fun switchVisibility() {
        content.isVisible = !content.isVisible

        if (content.isVisible) {
            val jTextField = content.getComponent(1) as? JTextField
            jTextField?.requestFocus()
            jTextField?.caretPosition = 0
        }
    }

}