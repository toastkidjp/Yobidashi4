/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.tool.file

import jp.toastkid.yobidashi4.domain.service.tool.file.NumericPathComparator
import org.koin.core.annotation.Single
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

@Single
class NumericPathComparatorImplementation : NumericPathComparator {

    override fun compare(o1: Path?, o2: Path?): Int {
        val o1Name = o1?.nameWithoutExtension ?: ""
        val o2Name = o2?.nameWithoutExtension ?: ""

        val o1Candidate = o1Name.substringAfterLast("_").toIntOrNull() ?: 0
        val o2Candidate = o2Name.substringAfterLast("_").toIntOrNull() ?: 0

        return o1Candidate.compareTo(o2Candidate)
    }

}