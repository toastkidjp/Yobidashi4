package jp.toastkid.yobidashi4.presentation.photo

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.IntOffset
import java.io.IOException
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.math.max

class PhotoTabViewModel {

    private val openMenu = mutableStateOf(false)

    private val scale = mutableStateOf(1f)

    private val rotationY = mutableStateOf(0f)

    private val rotationZ = mutableStateOf(0f)

    private val offset = mutableStateOf(Offset.Zero)

    private val state = TransformableState { zoomChange, offsetChange, rotationChange ->
        scale.value *= zoomChange
        rotationZ.value += rotationChange
        offset.value += offsetChange
    }

    private val colorFilterState = mutableStateOf<ColorFilter?>(null)

    private val alpha = mutableStateOf(0f)

    private val contrast = mutableStateOf(0f)

    private val focusRequester = FocusRequester()

    fun bitmap(source: Path): ImageBitmap? {
        val bufferedImage = try {
            ImageIO.read(source.toFile())
        } catch (e: IOException) {
            return null
        } ?: return null

        return bufferedImage.toComposeImageBitmap()
    }

    fun handleIconPath() = "images/icon/ic_${if (openMenu.value) "down" else "up"}.xml"

    fun switchMenu() {
        openMenu.value = openMenu.value.not()
    }

    fun visibleMenu() = openMenu.value

    fun alphaSliderPosition() = alpha.value

    fun setAlpha(it: Float) {
        alpha.value = it
        updateColorFilter()
    }

    fun contrast() = contrast.value

    fun setContrast(it: Float) {
        contrast.value = it
        updateColorFilter()
    }

    private fun updateColorFilter() {
        val v = max(contrast(), 0f) + 1f * (if (reverse.value) -1 else 1)
        val o = -128 * (v - 1)
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                v, 0f, 0f, alphaSliderPosition(), o,
                0f, v, 0f, alphaSliderPosition(), o,
                0f, 0f, v, alphaSliderPosition(), o,
                0f, 0f, 0f, 1f, 000f
            )
        )
        if (saturation.value) {
            colorMatrix.setToSaturation(0.0f)
        }
        colorFilterState.value = ColorFilter.colorMatrix(colorMatrix)
    }

    fun setSepia() {
        colorFilterState.value =
            if (colorFilterState.value != null) {
                null
            } else {
                ColorFilter.colorMatrix(
                    ColorMatrix(
                        floatArrayOf(
                            0.9f, 0f, 0f, 0f, 000f,
                            0f, 0.7f, 0f, 0f, 000f,
                            0f, 0f, 0.4f, 0f, 000f,
                            0f, 0f, 0f, 1f, 000f
                        )
                    )
                )
            }
    }

    private val saturation = mutableStateOf(false)

    fun saturation() {
        saturation.value = saturation.value.not()
        updateColorFilter()
    }

    fun offset() = IntOffset(
        offset.value.x.toInt(),
        offset.value.y.toInt()
    )

    fun setOffset(dragAmount: Offset) {
        offset.value += dragAmount.times(0.5f)
    }

    fun colorFilter() = colorFilterState.value

    fun scale() = scale.value

    fun rotationY() = rotationY.value

    fun rotationZ() = rotationZ.value

    fun state() = state

    fun onKeyEvent(it: KeyEvent): Boolean {
        return when {
            it.type != KeyEventType.KeyDown -> false
            it.isCtrlPressed && it.key == Key.Semicolon -> {
                scale.value = scale.value + 0.2f
                true
            }
            it.isCtrlPressed && it.key == Key.Minus -> {
                scale.value = scale.value - 0.2f
                true
            }
            else -> false
        }
    }

    fun focusRequester() = focusRequester

    fun flipImage() {
        rotationY.value = if (rotationY.value == 0f) 180f else 0f
    }

    private val reverse = mutableStateOf(false)

    fun reverseColor() {
        reverse.value = reverse.value.not()
        updateColorFilter()
    }

    fun launch() {
        focusRequester().requestFocus()
    }

    fun resetStates() {
        scale.value = 1f
        offset.value = Offset.Zero
        rotationY.value = 0f
        rotationZ.value = 0f
    }

}