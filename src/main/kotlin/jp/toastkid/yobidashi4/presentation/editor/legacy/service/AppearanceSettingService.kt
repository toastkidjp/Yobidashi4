package jp.toastkid.yobidashi4.presentation.editor.legacy.service

import java.awt.Color
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.presentation.editor.legacy.MenuCommand
import jp.toastkid.yobidashi4.presentation.editor.legacy.service.factory.FontSizeSpinnerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppearanceSettingService(private val channel: Channel<MenuCommand>? = null) : KoinComponent {

    private val setting: Setting by inject()

    private val sample = JTextField(SAMPLE_TEXT)

    private val contrastRatioCalculatorService = ContrastRatioCalculatorService()

    private val contrastRatioLabel = JLabel()

    operator fun invoke() {
        val content = JPanel()
        content.layout = GridBagLayout()
        val constraints = GridBagConstraints()
        constraints.gridx = 0
        constraints.gridy = 0

        sample.isEditable = false
        content.add(sample, constraints)

        constraints.gridy = 1
        content.add(contrastRatioLabel, constraints)

        applyColorSetting()

        constraints.gridx = 1
        constraints.gridy = 0

        val backgroundChooserButton = JButton("Background color").also {
            it.addActionListener {
                val color = ColorChooserService().invoke() ?: return@addActionListener
                setting.setEditorBackgroundColor(color)
                setting.save()
                applyColorSetting()
            }
        }
        content.add(backgroundChooserButton, constraints)

        constraints.gridy = 1
        val button2 = makeFontColorButton()
        content.add(button2, constraints)

        constraints.gridx = 1
        constraints.gridy = 2
        val button = JButton("Reset color setting").also {
            it.addActionListener {
                setting.resetEditorColorSetting()
                applyColorSetting()
            }
        }
        content.add(button, constraints)

        constraints.gridx = 2
        constraints.gridy = 0
        val fontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames
        val spinner = JComboBox<String>()
        val currentEditorFontFamily = setting.editorFontFamily()
        fontFamilyNames.forEach {
            spinner.addItem(it)
            if (it == currentEditorFontFamily) {
                spinner.selectedItem = it
            }
        }
        spinner.addItemListener {
            setting.setEditorFontFamily(it.item?.toString())
            applyColorSetting()
        }
        content.add(spinner, constraints)

        constraints.gridx = 2
        constraints.gridy = 1
        val sizeSpinner = FontSizeSpinnerFactory().invoke { applyColorSetting() }
        content.add(sizeSpinner, constraints)

        JOptionPane.showMessageDialog(null, content)
    }

    private fun applyColorSetting() {
        sample.foreground = setting.editorForegroundColor()
        sample.background = setting.editorBackgroundColor()
        sample.font = Font(setting.editorFontFamily(), Font.PLAIN, setting.editorFontSize())
        contrastRatioLabel.text =
                "Contrast ratio: ${contrastRatioCalculatorService(setting.editorBackgroundColor() ?: Color.BLACK, setting.editorForegroundColor() ?: Color.BLACK)}"
        CoroutineScope(Dispatchers.Default).launch {
            channel?.send(MenuCommand.REFRESH)
        }
    }

    private fun makeFontColorButton(): JComponent {
        return JButton("Font color").also {
            it.addActionListener {
                val color = ColorChooserService().invoke() ?: return@addActionListener
                setting.setEditorForegroundColor(color)
                setting.save()
                applyColorSetting()
            }
        }
    }

    companion object {

        private const val SAMPLE_TEXT = "あアA1@亜"

    }
}
