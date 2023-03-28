package jp.toastkid.yobidashi4.domain.service.archive

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.nio.file.Files
import java.util.stream.Stream
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class TopArticleLoaderServiceTest {

    @InjectMockKs
    private lateinit var topArticleLoaderService: TopArticleLoaderService

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        every { setting.articleFolderPath() }.returns(mockk())

        mockkStatic(Files::class)
        every { Files.list(any()) }.returns(Stream.of(mockk()))
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun invoke() {
        topArticleLoaderService.invoke()

        verify { setting.articleFolderPath() }
        verify { Files.list(any()) }
    }

}