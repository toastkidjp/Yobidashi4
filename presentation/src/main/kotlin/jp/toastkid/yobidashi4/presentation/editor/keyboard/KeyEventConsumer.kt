package jp.toastkid.yobidashi4.presentation.editor.keyboard

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.MultiParagraph
import jp.toastkid.yobidashi4.domain.model.web.search.SearchUrlFactory
import jp.toastkid.yobidashi4.domain.service.editor.text.TextReformat
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.BlockQuotation
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ExpressionTextCalculatorService
import jp.toastkid.yobidashi4.presentation.editor.markdown.text.ToHalfWidth
import jp.toastkid.yobidashi4.presentation.editor.usecase.TextEditorOperationUseCase
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class KeyEventConsumer(
    private val useCase: TextEditorOperationUseCase,
    private val mainViewModel: MainViewModel = object : KoinComponent { val vm : MainViewModel by inject() }.vm,
    private val controlAndLeftBracketCase: ControlAndLeftBracketCase = ControlAndLeftBracketCase(),
    private val selectedTextConversion: SelectedTextConversion = SelectedTextConversion(),
    private val searchUrlFactory: SearchUrlFactory = SearchUrlFactory(),
    private val toHalfWidth: ToHalfWidth = ToHalfWidth(),
    private val expressionTextCalculatorService: ExpressionTextCalculatorService = ExpressionTextCalculatorService(),
    private val blockQuotation: BlockQuotation = BlockQuotation(),
    private val textReformat: TextReformat = TextReformat(),
) {

    operator fun invoke(
        it: KeyEvent,
        content: TextFieldState,
        lastParagraph: MultiParagraph?
    ): Boolean {
        if (it.type != KeyEventType.KeyDown) {
            return false
        }

        return when {
            it.isCtrlPressed && it.key == Key.D -> {
                return useCase.duplicateLine()
            }
            it.isCtrlPressed && it.key == Key.Minus -> {
                return useCase.toListLines()
            }
            it.isCtrlPressed && it.key == Key.One -> {
                return useCase.toOrderedList()
            }
            it.isCtrlPressed && it.key == Key.Zero -> {
                return useCase.toTaskList()
            }
            it.isCtrlPressed && it.key == Key.Four -> {
                return useCase.moveToLineStart()
            }
            it.isCtrlPressed && it.key == Key.E -> {
                return useCase.moveToLineEnd()
            }
            it.isCtrlPressed && it.key == Key.Comma -> {
                return useCase.insertComma()
            }
            it.isCtrlPressed && it.key == Key.T -> {
                return useCase.toTable()
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.U -> {
                return useCase.switchCase()
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.H -> {
                useCase.toHalfWidth()
                true
            }
            it.isCtrlPressed && it.key == Key.B -> {
                return useCase.bold()
            }
            it.isCtrlPressed && it.key == Key.I -> {
                return useCase.italic()
            }
            it.isCtrlPressed && it.key == Key.Two -> {
                return useCase.doubleQuote()
            }
            it.isCtrlPressed && it.key == Key.Eight -> {
                return useCase.surroundBrackets()
            }
            it.isCtrlPressed && it.key == Key.LeftBracket -> {
                return useCase.controlAndLeftBracket()
            }
            it.isCtrlPressed && it.key == Key.RightBracket -> {
                useCase.surroundMultibyteBrackets()
                true
            }
            it.isCtrlPressed && it.key == Key.At -> {
                return useCase.surroundCodeFence()
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.C -> {
                return useCase.calculate()
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.N -> {
                useCase.switchEditable()
                true
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.O -> {
                return useCase.openUrl()
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.F -> {
                return useCase.reformat()
            }
            it.isCtrlPressed && it.isShiftPressed && it.key == Key.P -> {
                return useCase.prettyPrint()
            }
            it.isCtrlPressed && it.isAltPressed && it.key == Key.O -> {
                return useCase.search()
            }
            it.isCtrlPressed && it.key == Key.O -> {
                return useCase.openFile()
            }
            it.isCtrlPressed && it.key == Key.Q -> {
                return useCase.quote()
            }
            it.isCtrlPressed && it.key == Key.J -> {
                return useCase.joinLines()
            }
            it.isCtrlPressed && it.key == Key.L -> {
                return useCase.decorateLink()
            }
            else -> false
        }
    }

}
