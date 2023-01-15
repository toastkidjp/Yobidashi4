package jp.toastkid.yobidashi4.presentation.viewmodel.web

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue

interface WebTabViewModel {
    fun openFind(): Boolean
    fun switchFind()
    fun inputValue(): TextFieldValue
    fun onFindInputChange(id: String, value: TextFieldValue)
    fun findUp(id: String)
    fun findDown(id: String)
    fun switchDevTools()
    fun reload(id: String)
    @Composable
    fun view(id: String, initialUrl: String)
}