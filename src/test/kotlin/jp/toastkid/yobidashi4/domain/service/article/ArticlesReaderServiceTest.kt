package jp.toastkid.yobidashi4.domain.service.article

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Paths
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ArticlesReaderServiceTest {

    private lateinit var articlesReaderService: ArticlesReaderService

    @BeforeEach
    fun setUp() {
        mockkConstructor(Setting::class)
        every { anyConstructed<Setting>().articleFolder() }.returns("test")

        mockkStatic(Paths::class)
        every { Paths.get(any<String>()) }.returns(mockk())

        mockkStatic(Files::class)
        every { Files.list(any()) }.returns(mockk())

        articlesReaderService = ArticlesReaderService()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testInvoke() {
        articlesReaderService.invoke()

        verify(exactly = 1) { anyConstructed<Setting>().articleFolder() }
        verify(exactly = 1) { Paths.get(any<String>()) }
        verify(exactly = 1) { Files.list(any()) }
    }

}