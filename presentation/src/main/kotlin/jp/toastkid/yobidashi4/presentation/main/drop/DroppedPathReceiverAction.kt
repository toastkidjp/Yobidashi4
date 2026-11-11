/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.drop

import java.nio.file.Path

class DroppedPathReceiverAction(
    private val onReceive: (Path) -> Unit,
    private val onComplete: () -> Unit = {}
) {

    fun onReceive(path: Path) {
        this.onReceive.invoke(path)
    }

    fun onComplete() {
        this.onComplete.invoke()
    }

}