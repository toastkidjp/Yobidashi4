package jp.toastkid.yobidashi4.presentation.lib.input

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.domain.model.input.InputHistory
import jp.toastkid.yobidashi4.domain.repository.input.InputHistoryRepository
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.ParametersHolder

class InputHistoryService(private val context: String) : KoinComponent {

    private val viewModel: MainViewModel by inject()

    private val repository: InputHistoryRepository by inject(parameters = { ParametersHolder(mutableListOf(context)) })

    fun filter(items: MutableList<InputHistory>, query: String?) {
        items.clear()
        items.addAll(repository.filter(query).takeLast(5))
    }

    fun add(query: String?) {
        if (query.isNullOrBlank()) {
            return
        }

        repository.add(InputHistory(query, System.currentTimeMillis()))
    }

    fun make(query: String?): TextFieldValue? {
        if (query.isNullOrBlank()) {
            return null
        }

        return TextFieldValue("${query} ", TextRange(query.length + 1))
    }

    fun inputHistories(items: List<InputHistory>): List<String> = items.map { it.word }

    fun shouldShowInputHistory(items: List<InputHistory>): Boolean = items.isNotEmpty()

    fun delete(items: MutableList<InputHistory>, query: String) {
        repository.deleteWithWord(query)
        val filtered = items.filter { it.word != query }
        items.clear()
        items.addAll(filtered)
    }

    fun clear(items: MutableList<InputHistory>) {
        val swap = mutableListOf<InputHistory>().also {
            it.addAll(repository.list())
        }
        items.clear()
        repository.clear()
        viewModel.showSnackbar("It has done clear!", "Undo", {
            swap.forEach { repository.add(it) }
            items.addAll(swap.takeLast(5))
        })
    }

}