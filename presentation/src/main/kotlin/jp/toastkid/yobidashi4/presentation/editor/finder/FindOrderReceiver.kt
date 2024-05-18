package jp.toastkid.yobidashi4.presentation.editor.finder

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import java.util.concurrent.atomic.AtomicInteger
import jp.toastkid.yobidashi4.domain.model.find.FindOrder
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FindOrderReceiver : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    private val finderMessageFactory = FinderMessageFactory()

    private val selected = AtomicInteger(-1)

    operator fun invoke(it: FindOrder, content: TextFieldValue, setNewContent: (TextFieldValue) -> Unit) {
        if (it == FindOrder.EMPTY) {
            return
        }

        if (it.invokeReplace) {
            setNewContent(TextFieldValue(content.text.replace(it.target, it.replace, it.caseSensitive.not())))
            return
        }

        val newValue =
            if (it.upper) content.text.lastIndexOf(it.target, selected.get() - 1, it.caseSensitive.not())
            else content.text.indexOf(it.target, selected.get() + 1, it.caseSensitive.not())

        selected.set(newValue)

        val foundCount = if (it.target.isBlank()) 0 else content.text.split(it.target).size - 1
        mainViewModel.setFindStatus(finderMessageFactory(it.target, foundCount))

        if (selected.get() == -1) {
            setNewContent(content.copy(selection = TextRange(content.selection.start)))
            return
        }

        setNewContent(content.copy(selection = TextRange(selected.get(), selected.get() + it.target.length)))
    }

}