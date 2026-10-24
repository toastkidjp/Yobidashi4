/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.photo.gif

import jp.toastkid.yobidashi4.domain.service.photo.gif.GifDivider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

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