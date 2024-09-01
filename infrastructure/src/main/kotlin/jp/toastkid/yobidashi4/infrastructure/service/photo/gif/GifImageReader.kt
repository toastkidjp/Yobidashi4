package jp.toastkid.yobidashi4.infrastructure.service.photo.gif

import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO
import javax.imageio.ImageReader

class GifImageReader {

    operator fun invoke(path: Path, consumer: (BufferedImage, Int) -> Unit) {
        val it = ImageIO.getImageReadersByFormatName("gif")
        val imageReader: ImageReader = it.next()
        imageReader.input = ImageIO.createImageInputStream(path.toFile())
        val count: Int = imageReader.getNumImages(true)
        for (i in 0 until count) {
            consumer(imageReader.read(i), i)
        }
        imageReader.dispose()
    }

}