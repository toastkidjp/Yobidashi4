/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.web

import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import me.friwi.jcefmaven.CefAppBuilder
import me.friwi.jcefmaven.impl.progress.ConsoleProgressHandler
import org.cef.CefApp
import org.cef.callback.CefCommandLine
import org.cef.handler.CefAppHandlerAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path

class CefAppFactory : KoinComponent {

    private val appSetting : Setting by inject()

    operator fun invoke(): CefApp {
        val builder = CefAppBuilder()
        builder.setInstallDir(Path.of("jcef-bundle").toFile()) //Default
        builder.setProgressHandler(ConsoleProgressHandler()) //Default
        CefApp.addAppHandler(object : CefAppHandlerAdapter(arrayOf("--disable-gpu")) {
            override fun onBeforeCommandLineProcessing(processType: String, commandLine: CefCommandLine) {
                if (processType.isEmpty()) {
                    commandLine.appendSwitchWithValue("enable-media-stream", "true")
                    if (appSetting.darkMode()) {
                        commandLine.appendSwitchWithValue(
                            "blink-settings",
                            "forceDarkModeInversionAlgorithm=1,forceDarkModeEnabled=true"
                        )
                    }
                }
                super.onBeforeCommandLineProcessing(processType, commandLine)
            }
        })

        CefSettingsApplier().invoke(
            builder.cefSettings,
            UserAgent.findByName(appSetting.userAgentName()).text()
        )

        return builder.build()
    }

}