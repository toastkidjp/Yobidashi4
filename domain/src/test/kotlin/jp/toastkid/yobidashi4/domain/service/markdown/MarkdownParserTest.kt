package jp.toastkid.yobidashi4.domain.service.markdown

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.markdown.HorizontalRule
import jp.toastkid.yobidashi4.domain.model.markdown.TextBlock
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.ImageLine
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import kotlin.io.path.nameWithoutExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MarkdownParserTest {
    
    @InjectMockKs
    private lateinit var markdownParser: MarkdownParser

    @MockK
    private lateinit var path: Path
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { path.nameWithoutExtension } returns "test.md"

        mockkStatic(Files::class)
        every { Files.lines(path) } returns """
# Title
First description

## Image
![test](https://www.yahoo.co.jp/favicon.ico)

### Link
S&P 500 and [VIX](https://www.yahoo.co.jp/indices/VIX:IND)

---

## Table
| Time | Temperature
|:---|:---
| 8:10  | 29.4
| 8:25  | 29.5

## Sub
Test

## Code
```kotlin
println("Hello")
```

## Table2
| Key | Value |
|:---|:---|
| First | 422
| Second | 123
| Third | 3442
| Tax | 121

        """.split("\n").stream()
    }
    
    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val markdown = markdownParser.invoke(path)

        val lines = markdown.lines()
        assertAll(
            { assertEquals("test", markdown.title()) },
            { assertTrue(lines.any { it is TextBlock }) },
            { assertTrue(lines.any { it is CodeBlockLine }) },
            { assertTrue(lines.any { it is HorizontalRule }) },
            { assertEquals(2, lines.count { it is TableLine }) },
            { assertTrue(lines.any { it is ImageLine }) },
        )
    }

}