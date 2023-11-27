package jp.toastkid.yobidashi4.domain.service.tool.compound

import jp.toastkid.yobidashi4.domain.model.aggregation.CompoundInterestCalculationResult
import kotlin.math.roundToLong

class CompoundInterestCalculatorService {

    /**
     *
     * @param input If you want to calculate 5%, you should pass 0.05
     */
    operator fun invoke(input: CompoundInterestCalculatorInput): CompoundInterestCalculationResult {
        val (capital, installment, annualInterest, year) = input
        val result = CompoundInterestCalculationResult()
        var single = capital
        var compound = capital
        (1 .. year)
                .map { installment to installment }
                .forEachIndexed { index, _ ->
                    single += (installment * (1 + annualInterest)) + (capital * annualInterest)
                    compound += installment + (compound * annualInterest)

                    if (compound.isNaN()) {
                        return@forEachIndexed
                    }
                    result.put(index + 1, single.toLong(), compound.roundToLong())
                }
        return result
    }

}