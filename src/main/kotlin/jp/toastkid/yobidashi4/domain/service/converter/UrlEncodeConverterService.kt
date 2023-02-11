package jp.toastkid.yobidashi4.domain.service.converter

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class UrlEncodeConverterService : TwoStringConverterService {

    private val charset = StandardCharsets.UTF_8.name()

    override fun defaultFirstInputValue(): String {
        return "東京特許 許可局"
    }

    override fun defaultSecondInputValue(): String {
        return URLEncoder.encode("東京特許 許可局", charset)
    }

    override fun firstInputAction(input: String): String? {
        return URLEncoder.encode(input, charset)
    }

    override fun secondInputAction(input: String): String? {
        return URLDecoder.decode(input, charset)
    }

}