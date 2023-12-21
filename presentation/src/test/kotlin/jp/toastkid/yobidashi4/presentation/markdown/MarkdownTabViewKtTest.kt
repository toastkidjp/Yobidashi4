package jp.toastkid.yobidashi4.presentation.markdown

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.tab.MarkdownPreviewTab
import jp.toastkid.yobidashi4.domain.service.markdown.MarkdownParser
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import kotlin.io.path.nameWithoutExtension
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MarkdownTabViewKtTest {

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var tab: MarkdownPreviewTab

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { path.nameWithoutExtension } returns "test.md"

        mockkStatic(Files::class)
        every { Files.lines(any()) } returns """
# Title
First description

## Image
![test](https://www.yahoo.co.jp/favicon.ico)

### Link
S&P 500 and [VIX](https://www.yahoo.co.jp/indices/VIX:IND)

## List
- Aaron
- Beck
- Chief

## Ordered List
1. Soba
2. Udon
3. Tempra

## Task list
- [ ] Task1
- [x] Done

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
        every { tab.markdown() } returns MarkdownParser().invoke(path)
        every { tab.scrollPosition() } returns 0
        every { mainViewModel.finderFlow() } returns emptyFlow()
        every { mainViewModel.tabs } returns mutableStateListOf()

        startKoin {
            modules(
                module {
                    single(qualifier = null) { mainViewModel } bind (MainViewModel::class)
                }
            )
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun markdownTabView() {
        runDesktopComposeUiTest {
            setContent {
                MarkdownTabView(tab, Modifier)
            }
        }
    }
}