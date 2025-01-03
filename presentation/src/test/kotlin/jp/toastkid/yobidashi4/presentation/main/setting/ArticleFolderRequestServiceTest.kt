package jp.toastkid.yobidashi4.presentation.main.setting

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File
import javax.swing.JFileChooser

class ArticleFolderRequestServiceTest {

    private lateinit var subject: ArticleFolderRequestService

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        startKoin {
            modules(
                module {
                    single(qualifier = null) { setting } bind (Setting::class)
                }
            )
        }
        every { setting.setArticleFolderPath(any()) } just Runs
        every { setting.save() } just Runs
        mockkConstructor(JFileChooser::class)

        subject = ArticleFolderRequestService()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun invoke() {
        val file = mockk<File>()
        every { file.absolutePath } returns "/path/to/file"
        every { anyConstructed<JFileChooser>().selectedFile } returns file
        every { anyConstructed<JFileChooser>().showOpenDialog(any()) } returns JFileChooser.APPROVE_OPTION

        subject.invoke()

        verify { setting.setArticleFolderPath(any()) }
        verify { setting.save() }
        verify { file.absolutePath }
    }

    @Test
    fun invoke2() {
        every { anyConstructed<JFileChooser>().showOpenDialog(any()) } returns JFileChooser.CANCEL_OPTION

        subject.invoke()

        verify { setting wasNot called }
    }

}