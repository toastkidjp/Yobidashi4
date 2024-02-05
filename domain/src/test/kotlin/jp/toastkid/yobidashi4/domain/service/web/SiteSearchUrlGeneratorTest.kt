package jp.toastkid.yobidashi4.domain.service.web

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SiteSearchUrlGeneratorTest {

    @InjectMockKs
    private lateinit var subject: SiteSearchUrlGenerator

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun invoke() {
        assertEquals(
            "https://www.google.com/search?as_dt=i&as_sitesearch=www.yahoo.co.jp&as_q=test",
            subject.invoke("test", "https://www.yahoo.co.jp")
        )
    }

}