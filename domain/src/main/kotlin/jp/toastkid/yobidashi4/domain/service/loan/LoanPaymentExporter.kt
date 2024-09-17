package jp.toastkid.yobidashi4.domain.service.loan

import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.loan.Factor
import jp.toastkid.yobidashi4.domain.model.loan.LoanPayment

class LoanPaymentExporter {

    operator fun invoke(factor: Factor, payment: LoanPayment) {
        val folder = Path.of("user/loan")
        if (Files.exists(folder).not()) {
            Files.createDirectories(folder)
        }

        val resolve = folder.resolve("Payment_${factor.amount}_${factor.term}_${factor.interestRate}.txt")
        val lineSeparator = System.lineSeparator()
        Files.newBufferedWriter(resolve).use { writer ->
            writer.write("Amount: ${factor.amount}, Term: ${factor.term}, Interest rate: ${factor.interestRate}")
            writer.write(lineSeparator)
            writer.write("Down payment: ${factor.downPayment}")
            writer.write(lineSeparator)
            writer.write("Management fee: ${factor.managementFee}")
            writer.write(lineSeparator)
            writer.write("Renovation reserves: ${factor.renovationReserves}")
            writer.write(lineSeparator)
            writer.write(lineSeparator)
            writer.write("[Result]")
            writer.write(lineSeparator)
            writer.write("Monthly payment: ${payment.monthlyPayment}")
            writer.write(lineSeparator)
            payment.paymentSchedule.forEachIndexed() { index, schedule ->
                writer.write("${index + 1}\t${schedule.amount}\t${schedule.principal}\t${schedule.interest}")
                writer.write(lineSeparator)
            }
        }
    }

}