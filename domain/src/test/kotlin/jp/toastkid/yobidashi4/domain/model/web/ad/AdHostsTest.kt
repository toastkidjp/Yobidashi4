package jp.toastkid.yobidashi4.domain.model.web.ad

import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AdHostsTest {

    private lateinit var adHosts: AdHosts

    @BeforeEach
    fun setUp() {
        adHosts = AdHosts.make()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun contains() {
        assertTrue(adHosts.contains("https://test-ad.info/content.txt"))
        assertFalse(adHosts.contains("https://normal-site.com/content.txt"))
        assertFalse(adHosts.contains(null))
    }

}