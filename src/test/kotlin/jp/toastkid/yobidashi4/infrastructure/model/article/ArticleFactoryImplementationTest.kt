package jp.toastkid.yobidashi4.infrastructure.model.article

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.nio.file.Path
import jp.toastkid.yobidashi4.infrastructure.di.DiModule
import kotlin.test.assertEquals
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.ksp.generated.module

class ArticleFactoryImplementationTest {

    @InjectMockKs
    private lateinit var factory: ArticleFactoryImplementation

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        startKoin {
            modules(DiModule().module)
        }
        MockKAnnotations.init(this)
        mockkStatic(Path::class)
        every { Path.of(any(), any()) }.returns(path)
        every { path.fileName }.returns(Path.of("test.md"))
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