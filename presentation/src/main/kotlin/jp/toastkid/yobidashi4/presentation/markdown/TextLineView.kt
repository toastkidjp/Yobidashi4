package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextLineView(text: String, textStyle: TextStyle, modifier: Modifier) {
    val viewModel = remember { TextLineViewModel() }

    ClickableText(
        viewModel.annotatedString(),
        style = textStyle,
        onClick = {},
        modifier = modifier.onPointerEvent(PointerEventType.Release) {
            viewModel.onPointerReleased(it)
        },
        onTextLayout = { layoutResult ->
            viewModel.putLayoutResult(layoutResult)
        }
    )

    LaunchedEffect(text) {
        withContext(Dispatchers.IO) {
            viewModel.launch(text)
        }
    }
}
