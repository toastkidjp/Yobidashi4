package jp.toastkid.yobidashi4.domain.service.converter

interface TwoStringConverterService {

    fun title(): String

    fun defaultFirstInputValue(): String

    fun defaultSecondInputValue(): String

    fun firstInputAction(input: String): String?

    fun secondInputAction(input: String): String?

}