package jp.toastkid.yobidashi4.infrastructure.service.photo.gif

import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO
import javax.imageio.ImageWriter

class GifImageWriter {

    private val imageWriter: ImageWriter = ImageIO.getImageWritersByFormatName("gif").next()

    operator fun invoke(image: BufferedImage, outputTo: Path) {
        ImageIO.createImageOutputStream(outputTo.toFile()).use {
            imageWriter.setOutput(it)
            imageWriter.write(image)
            imageWriter.dispose()
        }
    }

}