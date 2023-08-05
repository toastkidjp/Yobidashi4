package jp.toastkid.yobidashi4.infrastructure.model.article

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class ArticleFactoryImplementationTest {

    @InjectMockKs
    private lateinit var factory: ArticleFactoryImplementation

    @MockK
    private lateinit var path: Path

    @MockK
    private lateinit var setting: Setting

    @MockK
    private lateinit var fileName: Path

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(
                module {
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }
        MockKAnnotations.init(this)
        mockkStatic(Path::class)
        every { Path.of(any(), any()) }.returns(path)
        every { path.fileName }.returns(fileName)
        every { fileName.toString() } returns "test.md"
        every { setting.articleFolder() } returns "test"
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun withTitle() {
        val article = factory.withTitle("test")

        assertEquals("test", article.getTitle())
    }
}