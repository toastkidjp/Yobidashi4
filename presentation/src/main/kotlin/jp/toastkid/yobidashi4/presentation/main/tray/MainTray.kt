package jp.toastkid.yobidashi4.presentation.main.tray

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun ApplicationScope.MainTray() {
    val viewModel = remember { MainTrayViewModel() }

    Tray(
        state = viewModel.trayState(),
        icon = painterResource(Res.drawable.icon),
        menu = {
            Item(
                "Open app folder",
                onClick = viewModel::openAppFolder
            )
            Item(
                "Open user folder",
                onClick = viewModel::openUserFolder

            )
            Item(
                "New chat",
                onClick = viewModel::openChat
            )
            Item("Exit", onClick = ::exitApplication)
        }
    )
}