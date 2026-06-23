/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.media

import jp.toastkid.yobidashi4.domain.model.setting.Setting
import jp.toastkid.yobidashi4.domain.service.media.MediaPlayerInvoker
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.absolutePathString

@Single
class MediaPlayerInvokerImplementation : KoinComponent, MediaPlayerInvoker {

    private val setting: Setting by inject()

    override operator fun invoke(mediaFilePath: Path) {
        try {
            Runtime.getRuntime().exec(
                arrayOf(
                    setting.mediaPlayerPath(),
                    mediaFilePath.absolutePathString()
                )
            )
        } catch (e: IOException) {
            LoggerFactory.getLogger(javaClass).warn("Runtime error.", e)
        }
    }

}
