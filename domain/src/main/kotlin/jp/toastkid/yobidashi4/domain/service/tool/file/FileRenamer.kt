/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.tool.file

import java.nio.file.Path

interface FileRenamer {

    operator fun invoke(paths: List<Path>, baseName: CharSequence, useResize: Boolean, onComplete: () -> Unit)

    fun makeRenamedFileName(baseName: CharSequence, i: Int, extension: String): String

}