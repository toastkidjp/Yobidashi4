/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.slideshow

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files

class SlideDeckReaderTest {

    private lateinit var slideDeckReader: SlideDeckReader

    @BeforeEach
    fun setUp() {
        slideDeckReader = SlideDeckReader(mockk())

        mockkStatic(Files::class)
        every { Files.lines(any()) } returns
"""
![background](
![background](https://www.yahoo.co.jp/all)

# Title slide
test

## Background
![background](https://www.yahoo.co.jp)

[footer](Incorrect footer note
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

```kotlin:test
val engine = KotlinJsr223DefaultScriptEngineFactory().scriptEngine
result.value = engine.eval(input.value.text).toString()
```

#### Quote
> To be, or not to be, that is the question.

# 完
        """.split("\n").stream()

    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val (slides, background, title, footerText) = slideDeckReader.invoke()

        assertEquals(7, slides.size)
        assertEquals("https://www.yahoo.co.jp/all", background)
        assertEquals("Title slide", title)
        assertTrue(slides.any { it.isFront() })
    }

    @Test
    fun exception() {
        val ioException = mockk<IOException>()
        every { Files.lines(any()) } throws ioException
        every { ioException.printStackTrace() } just Runs

        val (slides, background, title, footerText) = slideDeckReader.invoke()

        verify { Files.lines(any()) }
        verify { ioException.printStackTrace() }
        assertTrue(slides.isEmpty())
        assertTrue(background.isEmpty())
        assertTrue(title.isEmpty())
        assertTrue(footerText.isEmpty())
    }

    @Test
    fun lastTableCase() {
        every { Files.lines(any()) } returns
                """
## Table

| Time | ID | Title
|:---|:---|:---
| 09:30 -11:30 | D1-KY| Java Day Tokyo 2017 基調講演
| 13:00 -13:50 | D1-A1| Java 9 and Beyond: Java Renaissance in the Cloud
| 14:05 -14:55 | D1-A2| Modular Development with JDK 9""".split("\n").stream()

        val (slides, background, title, footerText) = slideDeckReader.invoke()

        assertEquals(1, slides.size)
        assertTrue(slides.last().lines().last() is TableLine)
    }

}