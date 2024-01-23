package jp.toastkid.yobidashi4.infrastructure.service.web

import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.model.web.user_agent.UserAgent
import me.friwi.jcefmaven.CefAppBuilder
import me.friwi.jcefmaven.impl.progress.ConsoleProgressHandler
import org.cef.CefApp
import org.cef.callback.CefCommandLine
import org.cef.handler.CefAppHandlerAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CefAppFactory : KoinComponent {

    private val appSetting : Setting by inject()

    operator fun invoke(): CefApp {
        val builder = CefAppBuilder()
        builder.setInstallDir(Path.of("jcef-bundle").toFile()) //Default
        builder.setProgressHandler(ConsoleProgressHandler()) //Default
        CefApp.addAppHandler(object : CefAppHandlerAdapter(arrayOf("--disable-gpu")) {
            override fun onBeforeCommandLineProcessing(processType: String?, commandLine: CefCommandLine?) {
                if (processType.isNullOrEmpty()) {
                    commandLine?.appendSwitchWithValue("enable-media-stream", "true")
                    if (appSetting.darkMode()) {
                        commandLine?.appendSwitchWithValue("blink-settings", "forceDarkModeInversionAlgorithm=1,forceDarkModeEnabled=true")
                    }
                }
                super.onBeforeCommandLineProcessing(processType, commandLine)
            }
        })
        CefSettingsApplier().invoke(builder.cefSettings, UserAgent.findByName(appSetting.userAgentName()).text())

        return builder.build()
    }

}