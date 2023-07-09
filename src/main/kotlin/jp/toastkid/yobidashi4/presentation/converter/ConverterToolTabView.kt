package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.service.converter.TatamiCountConverterService
import jp.toastkid.yobidashi4.domain.service.converter.TemperatureConverterService
import jp.toastkid.yobidashi4.domain.service.converter.UnixTimeConverterService
import jp.toastkid.yobidashi4.domain.service.converter.UrlEncodeConverterService

@Composable
fun ConverterToolTabView() {
    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            TwoValueConverterBox(UnixTimeConverterService())
            TwoValueConverterBox(UrlEncodeConverterService())
            TwoValueConverterBox(TatamiCountConverterService())
            TwoValueConverterBox(TemperatureConverterService())
        }
    }
}