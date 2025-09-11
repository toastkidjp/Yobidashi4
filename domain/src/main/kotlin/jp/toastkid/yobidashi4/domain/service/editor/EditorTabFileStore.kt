/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.editor

import jp.toastkid.yobidashi4.domain.model.tab.EditorTab
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.nio.file.Files

class EditorTabFileStore {

    operator fun invoke(tab: EditorTab, dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        if (tab.closeable()) {
            return
        }

        CoroutineScope(dispatcher).launch {
            val text = tab.getContent()
            val textArray = text.toString().toByteArray()

            if (textArray.isEmpty()) {
                return@launch
            }

            try {
                Files.write(tab.path, textArray)
                tab.setContent(text, true)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}