package jp.toastkid.yobidashi4.domain.model.web.user_agent

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Test of [UserAgent].
 *
 * @author toastkidjp
 */
class UserAgentTest {

    @Test
    fun testTitles() {
        val titles = UserAgent.titles()
        assertEquals(titles.javaClass, Array<String>::class.java)
    }

    @Test
    fun testFindIndex() {
        assertEquals(0, UserAgent.findCurrentIndex("tomato"))
        assertEquals(0, UserAgent.findCurrentIndex("Default"))
        assertEquals(4, UserAgent.findCurrentIndex("PC"))
    }

    @Test
    fun testFindByName() {
        assertEquals(UserAgent.DEFAULT, UserAgent.findByName("test"))
        assertEquals(UserAgent.DEFAULT, UserAgent.findByName("iphone"))
        assertEquals(UserAgent.IPHONE, UserAgent.findByName("IPHONE"))
    }

    @Test
    fun test() {
        assertEquals("Android", UserAgent.ANDROID.title())
        assertTrue(UserAgent.ANDROID.text().isNotEmpty())
    }
}
