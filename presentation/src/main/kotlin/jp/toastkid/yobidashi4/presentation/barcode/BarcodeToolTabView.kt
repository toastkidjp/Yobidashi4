package jp.toastkid.yobidashi4.presentation.barcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.presentation.viewmodel.barcode.BarcodeToolTabViewModel

@Composable
fun BarcodeToolTabView() {
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
                        .clickable {
                            viewModel.onClickImage()
                        }
                )
            }

            TextField(
                viewModel.encodeInputValue(),
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                label = { Text("Barcode text input") },
                textStyle = TextStyle(fontSize = 16.sp),
                onValueChange = {
                    viewModel.setEncodeInputValue(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Text("Decoder")

            TextField(
                viewModel.decodeInputValue(),
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                label = { Text("Barcode image URL") },
                textStyle = TextStyle(fontSize = 16.sp),
                onValueChange = {
                    viewModel.setDecodeInputValue(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            if (viewModel.decodeResult().isNotBlank()) {
                Surface(
                    elevation = 4.dp
                ) {
                    Text(
                        viewModel.decodeResult(),
                        modifier = Modifier.clickable {
                            viewModel.onClickDecodeResult()
                        }
                    )
                }
            }
        }
    }
}