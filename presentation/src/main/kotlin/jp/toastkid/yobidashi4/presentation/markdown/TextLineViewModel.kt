package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import jp.toastkid.yobidashi4.presentation.editor.preview.LinkBehaviorService
import jp.toastkid.yobidashi4.presentation.editor.preview.LinkGenerator
import jp.toastkid.yobidashi4.presentation.lib.text.KeywordHighlighter
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TextLineViewModel : KoinComponent {

    private val lastLayoutResult = mutableStateOf<TextLayoutResult?>(null)

    private val vm: MainViewModel by inject()

    private val finderTarget = vm.finderFlow()

    private val linkGenerator = LinkGenerator()

    private val linkBehaviorService = LinkBehaviorService()

    private val annotatedString = mutableStateOf(AnnotatedString(""))

    private val keywordHighlighter = KeywordHighlighter()

    private fun annotate(text: String, finderTarget: String? = null) = keywordHighlighter(text, finderTarget)

    fun annotatedString() = annotatedString.value

    suspend fun launch(text: String) {
        annotatedString.value = annotate(linkGenerator.invoke(text))

        finderTarget.collect {
            annotatedString.value = annotate(linkGenerator.invoke(text), it.target)
        }
    }

    fun putLayoutResult(layoutResult: TextLayoutResult) {
        lastLayoutResult.value = layoutResult
    }

    fun onPointerReleased(it: PointerEvent) {
        val textLayoutResult = lastLayoutResult.value ?: return
        val offset = textLayoutResult.getOffsetForPosition(it.changes.first().position)

        val stringRange = annotatedString
            .value
            .getStringAnnotations(tag = "URL", start = offset, end = offset)
            .firstOrNull() ?: return

        linkBehaviorService.invoke(stringRange.item)
    }

}