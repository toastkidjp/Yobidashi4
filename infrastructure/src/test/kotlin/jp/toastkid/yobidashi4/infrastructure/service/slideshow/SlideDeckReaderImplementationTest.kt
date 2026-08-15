package jp.toastkid.yobidashi4.infrastructure.service.slideshow

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import jp.toastkid.yobidashi4.domain.service.slideshow.SlideDeckReader
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path

class SlideDeckReaderImplementationTest {

    private lateinit var slideDeckReader: SlideDeckReader

    @MockK
    private lateinit var path: Path

    private val fakeFileSystem = FakeFileSystem()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        slideDeckReader = SlideDeckReaderImplementation(fakeFileSystem)

        val okioPath = "test.md".toPath()
        fakeFileSystem.write(okioPath) {
            writeUtf8(
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
        """
            )
        }
        path = okioPath.toNioPath()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val (slides, background, title, footerText) = slideDeckReader.invoke(path)

        assertEquals(7, slides.size)
        assertEquals("https://www.yahoo.co.jp/all", background)
        assertEquals("Title slide", title)
        assertTrue(slides.any { it.isFront() })
    }

    @Test
    fun exception() {
        val (slides, background, title, footerText) = slideDeckReader.invoke("doesNotExists".toPath().toNioPath())

        assertTrue(slides.isEmpty())
        assertTrue(background.isEmpty())
        assertTrue(title.isEmpty())
        assertTrue(footerText.isEmpty())
    }

    @Test
    fun lastTableCase() {
        val okioPath = "test2.md".toPath()
        fakeFileSystem.write(okioPath) {
            writeUtf8(
                """
## Table

| Time | ID | Title
|:---|:---|:---
| 09:30 -11:30 | D1-KY| Java Day Tokyo 2017 基調講演
| 13:00 -13:50 | D1-A1| Java 9 and Beyond: Java Renaissance in the Cloud
| 14:05 -14:55 | D1-A2| Modular Development with JDK 9"""
            )
        }

        val (slides, background, title, footerText) = slideDeckReader.invoke(okioPath.toNioPath())

        assertEquals(1, slides.size)
        assertTrue(slides.last().lines().last() is TableLine)
    }

}