/*
 * Copyright (c) 2022 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package jp.toastkid.yobidashi4.domain.repository.number

import java.nio.file.Path
import jp.toastkid.yobidashi4.domain.model.number.NumberPlaceGame

interface GameRepository {

    fun save(file: Path, game: NumberPlaceGame)

    fun load(file: Path): NumberPlaceGame?

    fun delete(file: Path?)

}