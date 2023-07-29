package jp.toastkid.yobidashi4.domain.service.article

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ArticleTitleGenerator {

    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd(E)").withLocale(Locale.ENGLISH)

    operator fun invoke(localDate: LocalDate = LocalDate.now()): String? = localDate.format(dateFormat)

}
