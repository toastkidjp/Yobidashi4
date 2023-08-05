package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

internal class KeywordSearchTest {

    private lateinit var keywordSearch: KeywordSearch

    @MockK
    private lateinit var path1: Path

    @MockK
    private lateinit var path2: Path

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        keywordSearch = KeywordSearch()

        MockKAnnotations.init(this)

        mockkStatic(Files::class)
        every { Files.readAllLines(any()) }.returns(listOf("tomato test"))
        every { Files.list(any()) } returns Stream.of(path1, path2)

        mockkStatic(Path::class)
        every { Path.of(any<String>()) } returns mockk()

        every { setting.articleFolder() } returns "test"

        val mockFile1 = mockk<File>()
        every { mockFile1.getName() }.returns("mockFile1")
        every { path1.toFile() }.returns(mockFile1)

        val mockFile2 = mockk<File>()
        every { mockFile2.getName() }.returns("mockFile2")
        every { path2.toFile() }.returns(mockFile2)
    }

    @Test
    fun test() {
        val result = keywordSearch.invoke("test", "mock")

        assertEquals(2, result.itemArrays().size)
    }

    @Test
    fun testUseTitleFilter() {
        val result = keywordSearch.invoke("test", "1")

        assertEquals(1, result.itemArrays().size)
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

}