package jp.toastkid.yobidashi4.presentation.editor.finder

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.text.TextRange
import jp.toastkid.yobidashi4.domain.model.find.FindOrder
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.atomic.AtomicInteger

class FindOrderReceiver : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val finderMessageFactory = FinderMessageFactory()

    private val selected = AtomicInteger(-1)

    operator fun invoke(it: FindOrder, content: TextFieldState) {
        if (it == FindOrder.EMPTY) {
            return
        }

        if (it.invokeReplace) {
            content.edit {
                val start = it.target.indexOf(it.replace, content.selection.start)
                if (start == -1) {
                    return@edit
                }
                replace(start, start + it.replace.length, it.replace)
            }
            return
        }

        val newValue =
            if (it.upper) content.text.lastIndexOf(it.target, selected.get() - 1, it.caseSensitive.not())
            else content.text.indexOf(it.target, selected.get() + 1, it.caseSensitive.not())

        selected.set(newValue)

        val foundCount = if (it.target.isBlank()) 0 else content.text.split(it.target).size - 1
        mainViewModel.setFindStatus(finderMessageFactory(it.target, foundCount))

        if (selected.get() == -1) {
            content.edit {
                selection = TextRange(content.selection.start)
            }
            return
        }

        content.edit {
            selection = TextRange(selected.get(), selected.get() + it.target.length)
        }
    }

}