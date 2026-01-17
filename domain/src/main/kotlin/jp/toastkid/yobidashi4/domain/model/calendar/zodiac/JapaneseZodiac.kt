/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.calendar.zodiac

class JapaneseZodiac {

    private val jikkan = listOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")

    private val junishi = listOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")

    fun calculate(year: Int): String {
        val jikkanIndex = (year - 4) % JIKKAN_SIZE
        val junishiIndex = (year - 4) % JUNISHI_SIZE

        val finalJikkanIndex = if (jikkanIndex < 0) jikkanIndex + JIKKAN_SIZE else jikkanIndex
        val finalJunishiIndex = if (junishiIndex < 0) junishiIndex + JUNISHI_SIZE else junishiIndex

        return jikkan[finalJikkanIndex] + junishi[finalJunishiIndex]
    }

}

private const val JIKKAN_SIZE = 10

private val JUNISHI_SIZE = 12
