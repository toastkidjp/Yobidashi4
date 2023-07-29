package jp.toastkid.yobidashi4.infrastructure.service.media

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.io.IOException
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class MediaPlayerInvokerTest {

    @InjectMockKs
    private lateinit var mediaPlayerInvoker: MediaPlayerInvokerImplementation

    @MockK
    private lateinit var runtime: Runtime

    @MockK
    private lateinit var setting: Setting

    @MockK
    private lateinit var path: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Runtime::class)
        every { Runtime.getRuntime() }.returns(runtime)
        every { runtime.exec(any<Array<String>>()) }.returns(mockk())
        every { setting.mediaPlayerPath() }.returns("path/to/media_player.ext")
        every { path.toAbsolutePath() }.returns(path)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun testInvoke() {
        mediaPlayerInvoker.invoke(path)

        verify { runtime.exec(any<Array<String>>()) }
    }

    @Test
    fun testErrorCase() {
        every { runtime.exec(any<Array<String>>()) }.throws(IOException())

        mediaPlayerInvoker.invoke(path)

        verify { runtime.exec(any<Array<String>>()) }
    }

}