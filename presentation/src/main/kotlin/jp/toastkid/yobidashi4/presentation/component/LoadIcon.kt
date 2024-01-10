package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
internal fun LoadIcon(iconPath: String?, modifier: Modifier = Modifier) {
    if (iconPath == null) {
        return
    }

    val viewModel = remember { LoadIconViewModel(iconPath) }
    if (viewModel.useIcon()) {
        Icon(
            painterResource(iconPath),
            contentDescription = viewModel.contentDescription(),
            tint = MaterialTheme.colors.onPrimary,
            modifier = modifier
        )
        return
    }

    val bitmap = viewModel.loadBitmap()
    if (bitmap != null) {
        Image(
            bitmap,
            contentDescription = viewModel.contentDescription(),
            modifier = modifier
        )
    } else {
        Icon(
            painterResource(viewModel.defaultIconPath()),
            contentDescription = viewModel.contentDescription(),
            tint = MaterialTheme.colors.onPrimary,
            modifier = modifier
        )
    }
}