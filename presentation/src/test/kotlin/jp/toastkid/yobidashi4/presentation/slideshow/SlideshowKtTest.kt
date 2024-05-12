package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.toOffset
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.awt.Image
import java.awt.image.BufferedImage
import java.net.URL
import java.nio.file.Files
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck
import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import jp.toastkid.yobidashi4.domain.service.slideshow.SlideDeckReader
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SlideshowKtTest {

    private lateinit var slideDeck: SlideDeck

    @BeforeEach
    fun setUp() {
        mockkStatic(Files::class)
        every { Files.lines(any()) } returns """
![background](https://www.yahoo.co.jp/all)

# Title slide
test

## Background
![background](https://www.yahoo.co.jp)

[footer](Footer text)

## Image
![image](https://www.yahoo.co.jp)

## Table

| Time | ID | Title
|:---|:---|:---
| 09:30 -11:30 | D1-KY| Java Day Tokyo 2017 基調講演
| 13:00 -13:50 | D1-A1| Java 9 and Beyond: Java Renaissance in the Cloud
| 14:05 -14:55 | D1-A2| Modular Development with JDK 9

## Code

```kotlin
val engine = KotlinJsr223DefaultScriptEngineFactory().scriptEngine
result.value = engine.eval(input.value.text).toString()
```

#### Quote
> To be, or not to be, that is the question.

# 完            
        """.trimIndent().split("\n").stream()
        mockkStatic(ImageIO::class)
        every { ImageIO.read(any<URL>()) } returns BufferedImage(1, 1, Image.SCALE_FAST)

        mockkConstructor(SlideshowViewModel::class)
        every { anyConstructed<SlideshowViewModel>().showSlider() } just Runs
        every { anyConstructed<SlideshowViewModel>().hideSlider() } just Runs

        slideDeck = SlideDeckReader(mockk()).invoke()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun slideshow() {
        runComposeUiTest {
            setContent {
                Slideshow(
                    slideDeck,
                    {},
                    {},
                    Modifier
                )
            }

            onNodeWithContentDescription("slider", useUnmergedTree = true)
                .assertExists("Not found!")
                .performMouseInput {
                    enter()
                    click()
                    press()
                    moveBy(IntOffset(-20, 0).toOffset())
                    release()
                    exit()
                }
                .performKeyInput {
                    pressKey(Key.K, 1000L)
                }
            verify { anyConstructed<SlideshowViewModel>().showSlider() }
            verify { anyConstructed<SlideshowViewModel>().hideSlider() }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun otherPage() {
        runComposeUiTest {
            setContent {
                Slideshow(
                    SlideDeck(slideDeck.slides.filter { it.lines().any { l -> l is ImageLine } }.toMutableList()),
                    {},
                    {},
                    Modifier
                )
            }
        }
    }
}