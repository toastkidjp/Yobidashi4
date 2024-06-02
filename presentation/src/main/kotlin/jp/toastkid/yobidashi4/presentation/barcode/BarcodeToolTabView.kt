package jp.toastkid.yobidashi4.presentation.barcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.presentation.component.SingleLineTextField

@Composable
internal fun BarcodeToolTabView() {
    val viewModel = remember { BarcodeToolTabViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Generator")

            viewModel.barcodeImage()?.let {
                Image(
                    it,
                    "Generated barcode",
                    modifier = Modifier.sizeIn(maxWidth = 400.dp, maxHeight = 400.dp)
                        .clickable(onClick = viewModel::onClickImage)
                )
            }

            SingleLineTextField(
                viewModel.encodeInputValue(),
                "Barcode text input",
                viewModel::setEncodeInputValue
            )

            Text("Decoder")

            SingleLineTextField(
                viewModel.decodeInputValue(),
                "Barcode image URL",
                viewModel::setDecodeInputValue
            )

            if (viewModel.decodeResult().isNotBlank()) {
                Surface(
                    elevation = 4.dp
                ) {
                    Text(
                        viewModel.decodeResult(),
                        modifier = Modifier.clickable(onClick = viewModel::onClickDecodeResult)
                    )
                }
            }
        }
    }
}