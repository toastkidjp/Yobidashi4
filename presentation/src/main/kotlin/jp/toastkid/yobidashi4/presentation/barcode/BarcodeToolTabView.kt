package jp.toastkid.yobidashi4.presentation.barcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.awt.image.BufferedImage
import jp.toastkid.yobidashi4.domain.service.barcode.BarcodeEncoder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun BarcodeToolTabView() {
    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        val lastBarcode = remember { mutableStateOf<BufferedImage?>(null) }
        val input = remember { mutableStateOf(TextFieldValue()) }
        val barcodeEncoder = remember { object : KoinComponent { val it: BarcodeEncoder by inject() }.it }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            lastBarcode.value?.toComposeImageBitmap()?.let {
                Image(
                    it,
                    "Generated barcode"
                )
            }

            TextField(
                input.value,
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
                label = { Text("Installment") },
                textStyle = TextStyle(fontSize = 16.sp),
                onValueChange = {
                    input.value = it

                    if (it.text.isNotBlank()) {
                        lastBarcode.value = barcodeEncoder.invoke(it.text, 400, 400)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}