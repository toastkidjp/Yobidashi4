/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.chat

enum class GenerativeAiModel(
    private val label: String,
    private val urlParameter: String,
    private val versionPath: String,
    private val webGrounding: Boolean = false,
    private val image: Boolean = false
) {

    GEMINI_3_0_FLASH(
        "Gemini 3 Flash",
        "gemini-3-flash-preview",
        "v1beta"
    ),
    GEMINI_2_5_FLASH(
        "Gemini 2.5 Flash",
        "gemini-2.5-flash",
        "v1beta",
        true
    ),
    GEMINI_2_5_FLASH_LITE(
        "Gemini 2.5 Flash Lite",
        "gemini-2.5-flash-lite",
        "v1beta",
        true
    ),
    GEMINI_2_5_FLASH_WITHOUT_WEB_GROUNDING(
        "Gemini 2.5 Flash(Web Grounding なし)",
        "gemini-2.5-flash",
        "v1beta",
    ),
    GEMINI_2_5_FLASH_LITE_WITHOUT_WEB_GROUNDING(
        "Gemini 2.5 Flash Lite(Web Grounding なし)",
        "gemini-2.5-flash-lite",
        "v1beta",
    ),
    ;

    fun label(): String = label

    fun url(): String {
        return "https://generativelanguage.googleapis.com/${versionPath}/models/${urlParameter}" +
                ":streamGenerateContent?alt=sse&key="
    }

    fun image() = image

    fun webGrounding() = webGrounding

    companion object {
        fun default() = GEMINI_2_5_FLASH_LITE
    }

}