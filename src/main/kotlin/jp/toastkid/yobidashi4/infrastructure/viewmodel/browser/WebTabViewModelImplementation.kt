package jp.toastkid.yobidashi4.infrastructure.viewmodel.browser

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.input.TextFieldValue
import jp.toastkid.yobidashi4.presentation.viewmodel.web.WebTabViewModel
import jp.toastkid.yobidashi4.presentation.web.BrowserView
import org.koin.core.annotation.Single

@Single
class WebTabViewModelImplementation : WebTabViewModel {
    private val browserView = BrowserView()

    private val openFind = mutableStateOf(false)

    private val findInput = mutableStateOf(TextFieldValue())

    override fun openFind() = openFind.value

    override fun switchFind() {
        openFind.value = openFind.value.not()
    }

    override fun inputValue() = findInput.value

    override fun onFindInputChange(id: String, value: TextFieldValue) {
        findInput.value = TextFieldValue(value.text, value.selection, value.composition)
        findDown(id)
    }

    override fun findUp(id: String) {
        browserView.findUp(id, inputValue().text)
    }

    override fun findDown(id: String) {
        browserView.find(id, inputValue().text)
    }

    override fun switchDevTools() {
        browserView.switchDevTools()
    }

    override fun reload(id: String) {
        browserView.reload(id)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun view(id: String, initialUrl: String) {
        browserView.view(id, initialUrl)
    }

}