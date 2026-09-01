/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.font.FontWeight
import jp.toastkid.yobidashi4.presentation.lib.keyboard.KeyboardDrivenScrollEventHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.koin.core.component.KoinComponent
import java.io.IOException
import java.net.URI
import javax.imageio.ImageIO

class MarkdownPreviewViewModel : KoinComponent {

    private val scrollEventFlow = MutableSharedFlow<Float>(extraBufferCapacity = 1)

    fun scrollEventFlow(): SharedFlow<Float> = scrollEventFlow

    private val keyboardDrivenScrollEventHandler = KeyboardDrivenScrollEventHandler()

    fun onKeyEvent(it: KeyEvent): Boolean {
        val result = keyboardDrivenScrollEventHandler.invoke(it)
        scrollEventFlow.tryEmit(result.delta)
        return result.consumed
    }

    fun extractText(it: String, taskList: Boolean): String {
        return if (taskList) it.substring(it.indexOf("] ") + 1) else it
    }

    fun loadBitmap(source: String): ImageBitmap? {
        val bufferedImage = try {
            ImageIO.read(URI(source).toURL())
        } catch (e: IllegalArgumentException) {
            return null
        } catch (e: IOException) {
            return null
        } ?: return null

        return bufferedImage.toComposeImageBitmap()
    }

    fun makeFontWeight(level: Int): FontWeight {
        return if (level != -1) FontWeight.Bold else FontWeight.Normal
    }

    private val showSubheadings = mutableStateOf(false)

    fun showSubheadings() = showSubheadings.value

    fun switchSubheadings() {
        showSubheadings.value = showSubheadings.value.not()
    }

}
