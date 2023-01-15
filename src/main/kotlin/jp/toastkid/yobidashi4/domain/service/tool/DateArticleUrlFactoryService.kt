package jp.toastkid.yobidashi4.domain.service.tool

import java.text.MessageFormat

class DateArticleUrlFactoryService {

    /**
     * Make Wikipedia article's url.
     *
     * @param month <b>1</b>-12
     * @param dayOfMonth 1-31
     *
     * @return Wikipedia article's url
     */
    operator fun invoke(month: Int, dayOfMonth: Int): String {
        if (month < 1 || month > 12) {
            return ""
        }
        if (dayOfMonth <= 0 || dayOfMonth >= 31) {
            return ""
        }

        return MessageFormat.format(FORMAT, month, dayOfMonth)
    }

    companion object {

        /**
         * Format resource ID.
         */
        private const val FORMAT = "https://ja.wikipedia.org/wiki/{0}月{1}日"

    }

}