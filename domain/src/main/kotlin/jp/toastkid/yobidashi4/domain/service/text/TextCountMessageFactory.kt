package jp.toastkid.yobidashi4.domain.service.text

class TextCountMessageFactory {

    operator fun invoke(target: String) =
        "Count: ${target.codePoints().count()} | Lines: ${target.trimEnd().count { it == '\n' } + 1}"

}