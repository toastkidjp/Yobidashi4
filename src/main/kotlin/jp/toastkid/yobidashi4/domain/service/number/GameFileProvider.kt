/*
 * Copyright (c) 2022 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package jp.toastkid.yobidashi4.domain.service.number

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

class GameFileProvider {

    operator fun invoke(): Path? {
        makeFolderIfNeed()

        return Paths.get(FOLDER_NAME).resolve(FIXED_FILE_NAME)
    }

    private fun makeFolderIfNeed() {
        val folder = Paths.get(FOLDER_NAME)
        if (Files.exists(folder).not()) {
            Files.createDirectories(folder)
        }

        val filePath = folder.resolve(FIXED_FILE_NAME)

        if (filePath.exists().not()) {
            Files.createFile(filePath)
        }
    }

    companion object {

        private const val FOLDER_NAME = "user/number/place/games"

        private const val FIXED_FILE_NAME = "saved_game"

    }
}