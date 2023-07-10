package jp.toastkid.yobidashi4.domain.service.converter

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class UrlEncodeConverterService : TwoStringConverterService {

    private val charset = StandardCharsets.UTF_8.name()

    override fun title(): String {
        return "URL Encoder"
    }

    override fun firstInputLabel(): String {
        return "Raw string"
    }

    override fun secondInputLabel(): String {
        return "Encoded string"
    }

    override fun defaultFirstInputValue(): String {
        return "東京特許 許可局"
    }

    override fun defaultSecondInputValue(): String {
        return "%E6%9D%B1%E4%BA%AC%E7%89%B9%E8%A8%B1+%E8%A8%B1%E5%8F%AF%E5%B1%80"
    }

    override fun firstInputAction(input: String): String? {
        return URLEncoder.encode(input, charset)
    }

    override fun secondInputAction(input: String): String? {
        return try {
            URLDecoder.decode(input, charset)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

}