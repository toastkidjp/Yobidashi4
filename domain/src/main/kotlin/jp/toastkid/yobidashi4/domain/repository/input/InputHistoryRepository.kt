/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.repository.input

import jp.toastkid.yobidashi4.domain.model.input.InputHistory

interface InputHistoryRepository {

    fun list(): List<InputHistory>

    fun filter(query: String?): List<InputHistory>

    fun add(item: InputHistory)

    fun delete(item: InputHistory)

    fun deleteWithWord(word: String)

    fun clear()

}