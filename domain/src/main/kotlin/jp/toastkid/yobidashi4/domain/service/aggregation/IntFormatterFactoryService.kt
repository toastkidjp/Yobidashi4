package jp.toastkid.yobidashi4.domain.service.aggregation

import java.text.NumberFormat
import javax.swing.text.NumberFormatter

class IntFormatterFactoryService {

    operator fun invoke(): NumberFormatter {
        val intFormatter = NumberFormatter(NumberFormat.getInstance())
        intFormatter.valueClass = Integer::class.java
        intFormatter.minimum = 0
        intFormatter.allowsInvalid = false
        return intFormatter
    }

}