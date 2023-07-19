package jp.toastkid.yobidashi4.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import java.nio.file.Path
import kotlin.io.path.inputStream
import org.slf4j.LoggerFactory

@Composable
internal fun LoadIcon(iconPath: String?, modifier: Modifier = Modifier) {
    iconPath ?: return
    val path = Path.of(iconPath)
    if (iconPath.contains("data").not()) {
        Icon(
            painterResource(iconPath),
            contentDescription = "Icon",
            tint = MaterialTheme.colors.onPrimary,
            modifier = modifier
        )
        return
    }

    val bitmap = path.inputStream().use { inputStream ->
        try {
            loadImageBitmap(inputStream)
        } catch (e: IllegalArgumentException) {
            LoggerFactory.getLogger("LoadIcon").debug("IllegalArgumentException by $path", e)
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap,
            contentDescription = "Icon",
            modifier = modifier
        )
    } else {
        Icon(
            painterResource("images/icon/ic_web.xml"),
            contentDescription = "Icon",
            tint = MaterialTheme.colors.onPrimary,
            modifier = modifier
        )
    }
}