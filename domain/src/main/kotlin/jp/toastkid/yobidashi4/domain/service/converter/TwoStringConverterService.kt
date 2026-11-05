/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.service.converter

interface TwoStringConverterService {

    fun title(): String

    fun firstInputLabel(): String

    fun secondInputLabel(): String

    fun defaultFirstInputValue(): String

    fun defaultSecondInputValue(): String

    fun firstInputAction(input: String): String?

    fun secondInputAction(input: String): String?

}