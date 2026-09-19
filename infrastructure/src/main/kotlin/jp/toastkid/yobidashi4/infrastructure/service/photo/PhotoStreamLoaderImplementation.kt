/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.photo

import jp.toastkid.yobidashi4.domain.service.photo.PhotoStreamLoader
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import okio.buffer
import org.koin.core.annotation.Single
import java.io.InputStream
import java.nio.file.Path

@Single
class PhotoStreamLoaderImplementation(
    private val fileSystem: FileSystem
) : PhotoStreamLoader{

    override fun invoke(path: Path): InputStream {
        return fileSystem.source(path.toOkioPath())
            .buffer()
            .inputStream()
    }

}