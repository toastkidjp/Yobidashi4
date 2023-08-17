package jp.toastkid.yobidashi4.infrastructure.service.web.icon

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IconUrlFinderTest {

    @InjectMockKs
    private lateinit var iconUrlFinder: IconUrlFinder

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun invoke() {
        val icons = iconUrlFinder.invoke(
            """
                <html>
                  <link rel="icon" class="js-site-favicon" type="image/svg+xml" href="https://github.githubassets.com/favicons/favicon.svg">
                  <link rel="icon" class="js-site-favicon" type="image/svg+xml" href="https://github.githubassets.com/favicons/favicon2.svg">
                  </html>
            """.trimIndent()
        )

        assertEquals(2, icons.size)
    }
}