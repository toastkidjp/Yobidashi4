package jp.toastkid.yobidashi4.presentation.main.drop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.extension
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TextFileReceiver(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    @Composable
    fun launch() {
        LaunchedEffect(mainViewModel.droppedPathFlow()) {
            withContext(dispatcher) {
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
    }

}