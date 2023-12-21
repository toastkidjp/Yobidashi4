package jp.toastkid.yobidashi4.presentation.slideshow

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.awt.Image
import java.awt.image.BufferedImage
import java.net.URL
import java.nio.file.Files
import javax.imageio.ImageIO
import jp.toastkid.yobidashi4.domain.model.slideshow.SlideDeck
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
        }
    }
}