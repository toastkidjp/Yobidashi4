package jp.toastkid.yobidashi4.infrastructure.service.web

import java.util.Locale
import org.cef.CefSettings

class CefSettingsApplier {

    operator fun invoke(settings: CefSettings, userAgentString: String) {
        settings.windowless_rendering_enabled = false //Default - select OSR mode
        settings.background_color = settings.ColorType(80, 0, 0, 0)
        settings.user_agent = userAgentString
        settings.locale = Locale.getDefault().language
    }

}