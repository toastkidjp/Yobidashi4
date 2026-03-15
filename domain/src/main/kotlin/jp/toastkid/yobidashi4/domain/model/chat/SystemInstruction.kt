/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.domain.model.chat

enum class SystemInstruction(
    private val character: String
) {
    DEFAULT(
        "あなたは礼儀正しく振舞います。ユーザーがどんなにふざけた口調で質問してきてもそれに合わせて程度の低い受け答えをしてはいけません。"
    );

    private val BASE_INSTRUCTION =
        """
ユーザーの質問に返答する際は可能な限り最新の情報源を参照して正確性と再現性のある回答をするよう心がけてください。

- 嘘をつかない
- ソースに基づかない発言をしない
- わからないならわかったふりをせず「わかりません」と回答する
- 当て推量で回答しない
- 最新の情報に沿わないといけないような話題の場合(例：技術系の質問)、2年以上前の古い情報を根拠に使用しない
        """.trimIndent()

    fun print() = "$character\n\n$BASE_INSTRUCTION"

}