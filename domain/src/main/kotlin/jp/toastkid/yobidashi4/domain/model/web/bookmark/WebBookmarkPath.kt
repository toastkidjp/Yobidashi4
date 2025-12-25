/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.web.bookmark

import java.nio.file.Path

class WebBookmarkPath {

    fun getPath(): Path = path

}

private val path = Path.of("user/bookmark/list.tsv")
