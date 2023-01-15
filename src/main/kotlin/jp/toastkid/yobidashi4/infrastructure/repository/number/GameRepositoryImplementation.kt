/*
 * Copyright (c) 2022 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package jp.toastkid.yobidashi4.infrastructure.repository.number

import java.nio.file.Files
import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.number.NumberPlaceGame
import jp.toastkid.yobidashi4.domain.repository.number.GameRepository
import kotlin.io.path.bufferedReader
import kotlin.io.path.exists
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class GameRepositoryImplementation : GameRepository {

    override fun save(file: Path, game: NumberPlaceGame) {
        val encodeToString = Json.encodeToString(game)
        val printWriter = Files.newBufferedWriter(file)
        printWriter.write(encodeToString)
        printWriter.flush()
    }

    override fun load(file: Path): NumberPlaceGame? {
        if (file.exists().not()) {
            return null
        }

        val string = file.bufferedReader().readText()
        if (string.isBlank()) {
            return null
        }
        return Json.decodeFromString(string)
    }

    override fun delete(file: Path?) {
        Files.delete(file)
    }

}