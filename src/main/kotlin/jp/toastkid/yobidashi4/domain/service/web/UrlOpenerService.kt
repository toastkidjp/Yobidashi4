package jp.toastkid.yobidashi4.domain.service.web

import java.awt.Desktop
import java.net.URI
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel

class UrlOpenerService(private val desktop: Desktop = Desktop.getDesktop()) {

    operator fun invoke(url: String?) {
        if (url.isNullOrBlank()) {
            return
        }

        invoke(URI(url))
    }

    operator fun invoke(uri: URI) {
        MainViewModel.get().openUrl(uri.toString(), false)
    }
}