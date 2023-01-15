package jp.toastkid.yobidashi4.domain.service.media

import java.io.IOException
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import kotlin.io.path.absolutePathString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory

class MediaPlayerInvoker : KoinComponent {

    private val setting: Setting by inject()

    operator fun invoke(mediaFilePath: Path) {
        try {
            Runtime.getRuntime().exec(
                arrayOf(
                    setting.mediaPlayerPath(),
                    mediaFilePath.absolutePathString()
                )
            )
        } catch (e: IOException) {
            LoggerFactory.getLogger(javaClass).warn("Runtime error.", e)
        }
    }

}
