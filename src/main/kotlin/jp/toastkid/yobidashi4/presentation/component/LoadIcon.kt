package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import java.nio.file.Paths
import kotlin.io.path.inputStream

@Composable
internal fun LoadIcon(iconPath: String?, modifier: Modifier = Modifier) {
    iconPath ?: return
    if (iconPath.contains("data")) {
        Paths.get(iconPath).inputStream().use { inputStream ->
            Image(
                loadImageBitmap(inputStream),
                contentDescription = "Icon",
                modifier = modifier
            )
        }
        return
    }

    Icon(
        painterResource(iconPath),
        contentDescription = "Icon",
        tint = MaterialTheme.colors.onPrimary,
        modifier = modifier
    )
}