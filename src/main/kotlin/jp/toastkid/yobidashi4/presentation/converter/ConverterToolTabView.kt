package jp.toastkid.yobidashi4.presentation.converter

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.service.converter.DistanceConverterService
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
        Box {
            val state = rememberScrollState()
            Column(modifier = Modifier.padding(8.dp).verticalScroll(state)) {
                TwoValueConverterBox(UnixTimeConverterService())
                TwoValueConverterBox(UrlEncodeConverterService())
                TwoValueConverterBox(TatamiCountConverterService())
                TwoValueConverterBox(TemperatureConverterService())
                TwoValueConverterBox(DistanceConverterService())
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(state),
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd)
            )
        }
    }
}