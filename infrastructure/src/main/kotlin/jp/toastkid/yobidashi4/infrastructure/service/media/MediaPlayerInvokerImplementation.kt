package jp.toastkid.yobidashi4.infrastructure.service.media

import java.io.IOException
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.media.MediaPlayerInvoker
import kotlin.io.path.absolutePathString
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory

@Single
class MediaPlayerInvokerImplementation : KoinComponent, MediaPlayerInvoker {

    private val setting: Setting by inject()

    override operator fun invoke(mediaFilePath: Path) {
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
