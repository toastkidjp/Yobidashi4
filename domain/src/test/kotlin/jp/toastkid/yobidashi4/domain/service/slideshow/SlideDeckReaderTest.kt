package jp.toastkid.yobidashi4.domain.service.slideshow

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Files
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SlideDeckReaderTest {

    private lateinit var slideDeckReader: SlideDeckReader

    @BeforeEach
    fun setUp() {
        slideDeckReader = SlideDeckReader(mockk())

        mockkStatic(Files::class)
        every { Files.lines(any()) } answers {
"""
![background](https://www.yahoo.co.jp/all)

# Title slide
test

## Background
![background](https://www.yahoo.co.jp)

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

# 完
        """.split("\n").stream()
        }
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val (slides, background, title, footerText) = slideDeckReader.invoke()

        assertEquals(6, slides.size)
        assertEquals("https://www.yahoo.co.jp/all", background)
        assertEquals("Title slide", title)
        assertTrue(slides.any { it.isFront() })
    }

}