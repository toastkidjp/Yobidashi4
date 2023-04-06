package jp.toastkid.yobidashi4.presentation.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ControlledComposition
import androidx.compose.runtime.DefaultMonotonicFrameClock
import androidx.compose.runtime.Recomposer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

fun callComposable(content: @Composable () -> Unit) {
    val c = ControlledComposition(UnitApplier(), createRecomposer())
    c.setContent(content)
}

private fun createRecomposer(): Recomposer {
    val mainScope = CoroutineScope(
        NonCancellable + Dispatchers.Unconfined + DefaultMonotonicFrameClock
    )

    return Recomposer(mainScope.coroutineContext).also {
        mainScope.launch(start = CoroutineStart.UNDISPATCHED) {
            it.runRecomposeAndApplyChanges()
        }
    }
}
