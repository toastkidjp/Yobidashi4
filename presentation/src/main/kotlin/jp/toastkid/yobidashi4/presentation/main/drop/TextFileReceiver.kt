package jp.toastkid.yobidashi4.presentation.main.drop

import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TextFileReceiver : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    suspend fun launch() {
        mainViewModel.droppedPathFlow().collect {
            when (it.extension) {
                "txt", "md", "log", "java", "kt", "py" -> {
                    mainViewModel.edit(it)
                }

                else -> Unit
            }
        }
    }

}