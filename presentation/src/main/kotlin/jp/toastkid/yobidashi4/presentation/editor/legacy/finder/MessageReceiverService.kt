package jp.toastkid.yobidashi4.presentation.editor.legacy.finder

import javax.swing.JLabel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MessageReceiverService(
    private val messageChannel: Channel<String>,
    private val message: JLabel
) {

    operator fun invoke() {
        CoroutineScope(Dispatchers.Default).launch {
            messageChannel.receiveAsFlow().collect {
                message.text = it
            }
        }
    }

}