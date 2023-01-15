package jp.toastkid.yobidashi4.domain.service.tool.compound

import java.awt.GridLayout
import javax.swing.JFormattedTextField
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.text.NumberFormatter
import jp.toastkid.yobidashi4.domain.service.aggregation.IntFormatterFactoryService

class CompoundInterestCalculationInputService(
        private val intFormatter: NumberFormatter = IntFormatterFactoryService().invoke()
) {

    operator fun invoke(): Triple<Int, Double, Int>? {
        val installmentInput = JFormattedTextField(intFormatter)
        val annualInterestInput = JFormattedTextField(intFormatter)
        val yearInput = JFormattedTextField(intFormatter)
        val content = makeContent(installmentInput, annualInterestInput, yearInput)

        val option = JOptionPane.showConfirmDialog(
                null,
                content,
            "Compound interest calculation",
            JOptionPane.DEFAULT_OPTION
        )

        if (option != JOptionPane.OK_OPTION
                || installmentInput.text.isNullOrBlank()
                || annualInterestInput.text.isNullOrBlank()
                || yearInput.text.isNullOrBlank()
        ) {
            return null
        }

        val installment = installmentInput.text.replace(REPLACE_TARGET, "").toInt()
        val annualInterest = annualInterestInput.text.replace(REPLACE_TARGET, "").toDouble()
        val year = yearInput.text.replace(REPLACE_TARGET, "").toInt()

        return Triple(installment, annualInterest, year)
    }

    private fun makeContent(
        installmentInput: JFormattedTextField,
        annualInterestInput: JFormattedTextField,
        yearInput: JFormattedTextField
    ): JPanel {
        return JPanel().also {
            it.layout = GridLayout(3, 2)
            it.add(JLabel("Installment"))
            it.add(installmentInput)
            it.add(JLabel("Annual interest"))
            it.add(annualInterestInput)
            it.add(JLabel("Year"))
            it.add(yearInput)
        }
    }

    companion object {

        private const val REPLACE_TARGET = ","

    }

}