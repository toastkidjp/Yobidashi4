package jp.toastkid.yobidashi4.infrastructure.service.photo.gif

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.service.photo.gif.GifDivider
import kotlin.io.path.nameWithoutExtension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import org.slf4j.LoggerFactory

@Single
class GifDividerImplementation : GifDivider {

    override suspend operator fun invoke(path: Path) {
        try {
            val folder = path.resolveSibling(path.nameWithoutExtension)
            withContext(Dispatchers.IO) {
                Files.createDirectory(folder)
            }

            withContext(Dispatchers.IO) {
                val imageWriter = GifImageWriter()
                GifImageReader().invoke(path) { image, i ->
                    imageWriter.invoke(
                        image,
                        folder.resolve("${path.nameWithoutExtension}_${i}.png")
                    )
                    image.flush()
                }
            }
        } catch (e: IOException) {
            LoggerFactory.getLogger(javaClass).error("Gif dividing.", e)
        }
    }

}