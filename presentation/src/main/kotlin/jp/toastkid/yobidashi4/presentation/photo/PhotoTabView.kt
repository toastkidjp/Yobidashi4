package jp.toastkid.yobidashi4.presentation.photo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateRotateBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import jp.toastkid.yobidashi4.domain.model.tab.PhotoTab
import jp.toastkid.yobidashi4.library.resources.Res
import jp.toastkid.yobidashi4.library.resources.ic_brush
import jp.toastkid.yobidashi4.library.resources.ic_flip
import jp.toastkid.yobidashi4.library.resources.ic_rotate_left
import jp.toastkid.yobidashi4.library.resources.ic_rotate_right
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.io.path.extension
import kotlin.io.path.name


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PhotoTabView(tab: PhotoTab) {
    val viewModel = remember { PhotoTabViewModel() }

    val coroutineScope = rememberCoroutineScope()

    Box(
        Modifier
            .onKeyEvent(viewModel::onKeyEvent)
            .focusable(true)
            .focusRequester(viewModel.focusRequester())
    ) {
        Image(
            viewModel.bitmap(),
            contentDescription = tab.path().name,
            colorFilter = viewModel.colorFilter(),
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = viewModel.scale(),
                    scaleY = viewModel.scale(),
                    rotationY = viewModel.rotationY(),
                    rotationZ = viewModel.rotationZ()
                )
                .offset {
                    viewModel.offset()
                }
                .transformable(state = viewModel.state())
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        viewModel.setOffset(dragAmount)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { viewModel.resetStates() }
                    )
                }
        )

        Surface(
            elevation = 4.dp,
            modifier = Modifier.align(Alignment.BottomCenter).alpha(animateFloatAsState(viewModel.handleAlpha()).value)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .onPointerEvent(PointerEventType.Enter) {
                        viewModel.showHandle()
                    }
                    .onPointerEvent(PointerEventType.Exit) {
                        viewModel.hideHandle()
                    }
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clickable(onClick = viewModel::switchMenu)
                        .fillMaxWidth()
                        .semantics { contentDescription = "Switch menu" }
                ) {
                    Icon(
                        painterResource(viewModel.handleIconPath()),
                        contentDescription = "handle",
                        modifier = Modifier.padding(8.dp)
                    )
                }

                if (viewModel.visibleMenu()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Alpha")

                        Slider(
                            viewModel.alphaSliderPosition(),
                            onValueChange = viewModel::setAlpha,
                            valueRange = -5f .. 1f,
                            steps = 600
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Contrast")

                        Slider(
                            viewModel.contrast(),
                            onValueChange = viewModel::setContrast,
                            valueRange = 0f .. 10f,
                            steps = 1000
                        )
                    }

                    Row(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            painterResource(Res.drawable.ic_rotate_left),
                            contentDescription = "Rotation left",
                            tint = MaterialTheme.colors.onSurface,
                            modifier = Modifier.clickable {
                                coroutineScope.launch {
                                    viewModel.state().animateRotateBy(-90f)
                                }
                            }
                        )

                        Icon(
                            painterResource(Res.drawable.ic_rotate_right),
                            contentDescription = "Rotation right",
                            tint = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .clickable {
                                    coroutineScope.launch {
                                        viewModel.state().animateRotateBy(90f)
                                    }
                                }
                                .padding(start = 16.dp)
                        )

                        Icon(
                            painterResource(Res.drawable.ic_flip),
                            contentDescription = "Flip image",
                            tint = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .clickable(onClick = viewModel::flipImage)
                                .padding(start = 16.dp)
                        )

                        Icon(
                            painterResource(Res.drawable.ic_brush),
                            contentDescription = "Reverse color",
                            tint = Color(0xCCCDDC39),
                            modifier = Modifier
                                .clickable(onClick = viewModel::reverseColor)
                                .padding(start = 16.dp)
                        )

                        Icon(
                            painterResource(Res.drawable.ic_brush),
                            contentDescription = "Sepia filter",
                            tint = Color(0xDDFF5722),
                            modifier = Modifier
                                .clickable(onClick = viewModel::setSepia)
                                .padding(start = 16.dp)
                        )

                        Icon(
                            painterResource(Res.drawable.ic_brush),
                            contentDescription = "Saturation",
                            tint = Color(0xFFAAAAAA),
                            modifier = Modifier
                                .clickable(onClick = viewModel::saturation)
                                .padding(start = 16.dp)
                        )

                        if (tab.path().extension == "gif") {
                            Icon(
                                painterResource(Res.drawable.ic_brush),
                                contentDescription = "Divide GIF",
                                tint = MaterialTheme.colors.onSurface,
                                modifier = Modifier.clickable {
                                    viewModel.divideGif(tab.path())
                                }
                                    .padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(tab) {
        viewModel.launch(tab.path())
    }
}