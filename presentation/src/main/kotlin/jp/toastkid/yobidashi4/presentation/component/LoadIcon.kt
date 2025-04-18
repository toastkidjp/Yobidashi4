package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import jp.toastkid.yobidashi4.domain.model.tab.Tab
import jp.toastkid.yobidashi4.domain.model.tab.WebTab
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun TabIcon(tab: Tab, modifier: Modifier = Modifier) {
    val viewModel = remember { LoadIconViewModel() }

    val icon = viewModel.loadTabIcon(tab)
    if (icon != null) {
        Icon(
            painterResource(icon),
            contentDescription = viewModel.contentDescription(),
            tint = MaterialTheme.colors.onPrimary,
            modifier = modifier
        )
        return
    }

    if (tab !is WebTab) {
        return
    }

    val bitmap = viewModel.loadBitmap(tab.url())
    if (bitmap != null) {
        Image(
            bitmap,
            contentDescription = viewModel.contentDescription(),
            modifier = modifier
        )
    }
}

@Composable
internal fun LoadIcon(url: String?, modifier: Modifier = Modifier) {
    if (url == null) {
        return
    }

    val viewModel = remember { LoadIconViewModel() }

    val bitmap = viewModel.loadBitmap(url)
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