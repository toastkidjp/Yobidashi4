package jp.toastkid.yobidashi4.presentation.editor.preview

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LinkGeneratorTest {

    @InjectMockKs
    private lateinit var linkBehaviorService: LinkBehaviorService

    @MockK
    private lateinit var exists: (String) -> Boolean

    @MockK
    private lateinit var internalLinkScheme: InternalLinkScheme

    @Suppress("unused")
    private val mainDispatcher = Dispatchers.Unconfined

    @Suppress("unused")
    private val ioDispatcher = Dispatchers.Unconfined

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { internalLinkScheme.isInternalLink(any()) }.returns(true)
        every { internalLinkScheme.extract(any()) }.returns("yahoo")
    }

    @Test
    fun testNullUrl() {
        linkBehaviorService.invoke(null)
    }

    @Test
    fun testEmptyUrl() {
        linkBehaviorService.invoke("")
    }

    @Test
    fun testWebUrl() {
        every { internalLinkScheme.isInternalLink(any()) }.returns(false)

        linkBehaviorService.invoke("https://www.yahoo.co.jp")
    }

    @Test
    fun testArticleUrlDoesNotExists() {
        coEvery { exists(any()) }.answers { false }

        linkBehaviorService.invoke("internal-article://yahoo")
    }

    @Test
    fun testArticleUrl() {
        coEvery { exists(any()) }.answers { true }

        linkBehaviorService.invoke("internal-article://yahoo")
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

}