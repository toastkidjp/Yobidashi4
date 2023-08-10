package jp.toastkid.yobidashi4.infrastructure.service.web

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.cef.CefSettings
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CefSettingsApplierTest {

    @InjectMockKs
    private lateinit var cefSettingsApplier: CefSettingsApplier

    @MockK
    private lateinit var settings: CefSettings

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        /*every { settings setProperty "windowless_rendering_enabled" value any<Boolean>() } just Runs
        every { settings setProperty "background_color" value any<CefSettings.ColorType>() } just Runs
        every { settings setProperty "user_agent" value any<String>() } just Runs
        every { settings setProperty "locale" value  any<String>() } just Runs*/
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun invoke() {
        cefSettingsApplier.invoke(settings, "dummy-user-agent")
    }
}