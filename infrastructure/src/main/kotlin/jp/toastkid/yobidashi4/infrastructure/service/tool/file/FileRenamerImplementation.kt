/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.tool.file

import jp.toastkid.yobidashi4.domain.service.io.IoContextProvider
import jp.toastkid.yobidashi4.domain.service.tool.file.FileRenamer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.image.BufferedImage
import java.nio.file.Path
import java.text.DecimalFormat
import java.util.concurrent.atomic.AtomicReference
import javax.imageio.ImageIO
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension
import kotlin.math.max

@Single
class FileRenamerImplementation(
    private val fileSystem: FileSystem
) : FileRenamer, KoinComponent {

    private val ioContextProvider: IoContextProvider by inject()

    override fun invoke(paths: List<Path>, baseName: CharSequence, useResize: Boolean, onComplete: () -> Unit) {
        if (paths.isEmpty()) {
            return
        }

        val decimalFormat = makeDecimalFormat(paths.size)
        CoroutineScope(ioContextProvider()).launch {
            paths.forEachIndexed { i, p ->
                val renamedPath = AtomicReference(p.resolveSibling(makeRenamedFileName(baseName, i, p.extension)))
                var maxAttempts = 4
                while (fileSystem.exists(renamedPath.get().toOkioPath())) {
                    val current = renamedPath.get()
                    renamedPath.set(p.resolveSibling("${current.nameWithoutExtension}_.${current.extension}"))
                    maxAttempts--
                }

                if (useResize) {
                    resizeImageToHalf(p, renamedPath.get())
                    return@forEachIndexed
                }

                fileSystem.copy(p.toOkioPath(), renamedPath.get().toOkioPath())
            }

            withContext(Dispatchers.Unconfined) {
                onComplete()
            }
        }
    }

    private fun makeDecimalFormat(listSize: Int): DecimalFormat {
        val decimalFormat = DecimalFormat("0".repeat(listSize.toString().length))
        return decimalFormat
    }

    /**
     * 画像の縦横サイズを半分にして保存する関数
     * @return 処理が成功したかどうか
     */
    fun resizeImageToHalf(sourcePath: Path, targetPath: Path): Boolean {
        val originalImage: BufferedImage = ImageIO.read(sourcePath.toFile()) ?: return false

        val newWidth = max(1, originalImage.width / 2)
        val newHeight = max(1, originalImage.height / 2)

        val resizedImage = BufferedImage(newWidth, newHeight, originalImage.type.let {
            if (it == BufferedImage.TYPE_CUSTOM) BufferedImage.TYPE_INT_ARGB else it
        })

        val g2d = resizedImage.createGraphics()
        try {
            g2d.setRenderingHint(
                java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR
            )
            g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null)
        } finally {
            g2d.dispose()
        }

        val formatName = sourcePath.extension.ifEmpty { "png" }
        return ImageIO.write(resizedImage, formatName, targetPath.toFile())
    }

    override fun makeRenamedFileName(baseName: CharSequence, i: Int, extension: String): String =
        "${baseName}_${makeDecimalFormat(i).format(i + 1)}.$extension"

}
