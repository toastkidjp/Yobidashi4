package jp.toastkid.yobidashi4.presentation.main.tray

import androidx.compose.runtime.mutableStateListOf
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.chat.Chat
import jp.toastkid.yobidashi4.domain.model.tab.ChatTab
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainTrayViewModel : KoinComponent {

    private val mainViewModel: MainViewModel by inject()

    fun trayState() = mainViewModel.trayState()

    fun openAppFolder() = mainViewModel.openFile(Path.of("."))

    fun openUserFolder() = mainViewModel.openFile(Path.of("user"))

    fun openChat() = mainViewModel.openTab(ChatTab(Chat(mutableStateListOf())))

}