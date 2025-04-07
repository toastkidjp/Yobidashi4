package jp.toastkid.yobidashi4.presentation.editor.markdown.text

import java.text.Normalizer

class ToHalfWidth {

    operator fun invoke(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFKC)
    }

}