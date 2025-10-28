/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.chat

import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GenerativeAiModelTest {

    @Test
    fun image() {
        assertTrue(GenerativeAiModel.GEMINI_2_0_FLASH_IMAGE.image())

        assertTrue(
            GenerativeAiModel.entries
                .filter { it != GenerativeAiModel.GEMINI_2_0_FLASH_IMAGE }
                .none(GenerativeAiModel::image)
        )
    }

    @Test
    fun default() {
        assertSame(GenerativeAiModel.GEMINI_2_5_FLASH_LITE, GenerativeAiModel.default())
    }

}