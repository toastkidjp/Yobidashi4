package jp.toastkid.yobidashi4.infrastructure.service.web

import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import jp.toastkid.yobidashi4.infrastructure.model.web.Context
import jp.toastkid.yobidashi4.infrastructure.model.web.ContextMenu
import org.cef.callback.CefContextMenuParams
import org.cef.callback.CefMenuModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CefContextMenuFactoryTest {

    @InjectMockKs
    private lateinit var cefContextMenuFactory: CefContextMenuFactory

    @MockK
    private lateinit var model: CefMenuModel

    @MockK
    private lateinit var params: CefContextMenuParams

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { model.addItem(any(), any()) } returns true
        every { params.sourceUrl } returns "https://www.yahoo.co.jp/favicon.ico"
        every { params.linkUrl } returns "https://www.yahoo.co.jp/link"
        every { params.pageUrl } returns "https://www.yahoo.co.jp"
    }

    @Test
    fun test() {
        cefContextMenuFactory.invoke(params, model)

        ContextMenu.values().filter { it.context != Context.PLAIN_TEXT }.forEach {
            verify(exactly = 1) { model.addItem(it.id, it.text) }
        }
    }

    @Test
    fun testPlainTextCase() {
        every { params.sourceUrl } returns null
        every { params.linkUrl } returns null

        cefContextMenuFactory.invoke(params, model)

        ContextMenu.values().filter { it.context == Context.PLAIN_TEXT }.forEach {
            verify(exactly = 1) { model.addItem(it.id, it.text) }
        }
    }

    @Test
    fun testPlainTextCaseWithBlank() {
        every { params.sourceUrl } returns " "
        every { params.linkUrl } returns " "

        cefContextMenuFactory.invoke(params, model)

        ContextMenu.values().filter { it.context == Context.PLAIN_TEXT }.forEach {
            verify(exactly = 1) { model.addItem(it.id, it.text) }
        }
    }

    @Test
    fun modelIsNull() {
        cefContextMenuFactory.invoke(params, null)

        verify { params wasNot called }
    }

}