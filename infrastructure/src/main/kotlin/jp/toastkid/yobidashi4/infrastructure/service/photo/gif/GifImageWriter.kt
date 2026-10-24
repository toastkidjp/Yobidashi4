/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
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