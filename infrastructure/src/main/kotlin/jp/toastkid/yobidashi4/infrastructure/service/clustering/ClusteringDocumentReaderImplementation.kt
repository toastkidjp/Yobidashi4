/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.clustering

import jp.toastkid.yobidashi4.domain.service.tool.clustering.ClusteringDocumentReader
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import okio.buffer
import org.koin.core.annotation.Single
import java.nio.file.Path
import kotlin.io.path.name

@Single
class ClusteringDocumentReaderImplementation(
    private val fileSystem: FileSystem
) : ClusteringDocumentReader {

    override fun invoke(paths: Collection<Path>): List<Pair<String, String>> {
        return paths.map { it.name to fileSystem.source(it.toOkioPath()).buffer().use(BufferedSource::readUtf8) }.toList()
    }

}