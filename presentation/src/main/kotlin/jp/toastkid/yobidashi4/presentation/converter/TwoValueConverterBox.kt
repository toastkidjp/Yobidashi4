package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.service.converter.TwoStringConverterService
import jp.toastkid.yobidashi4.presentation.component.SingleLineTextField

@Composable
fun TwoValueConverterBox(unixTimeConverterService: TwoStringConverterService) {
    val viewModel = remember { TwoValueConverterBoxViewModel(unixTimeConverterService) }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Column {
            Text(unixTimeConverterService.title(), modifier = Modifier.padding(8.dp))

            SingleLineTextField(
                viewModel.firstInput(),
                unixTimeConverterService.firstInputLabel(),
                viewModel::onFirstValueChange,
                viewModel::clearFirstInput,
                KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            SingleLineTextField(
                viewModel.secondInput(),
                unixTimeConverterService.secondInputLabel(),
                viewModel::onSecondValueChange,
                viewModel::clearSecondInput
            )
        }
    }
}