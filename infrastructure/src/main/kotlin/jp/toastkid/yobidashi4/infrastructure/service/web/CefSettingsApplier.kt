/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.web

import org.cef.CefSettings
import java.util.Locale

class CefSettingsApplier {

    operator fun invoke(settings: CefSettings, userAgentString: String) {
        settings.windowless_rendering_enabled = false //Default - select OSR mode
        settings.background_color = settings.ColorType(80, 0, 0, 0)
        settings.user_agent = userAgentString
        settings.locale = Locale.getDefault().language
    }

}