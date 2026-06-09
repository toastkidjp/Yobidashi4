/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.lib.text

import androidx.compose.ui.text.style.TextDecoration.Companion.Underline
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class KeywordHighlighterTest {

    private lateinit var subject: KeywordHighlighter

    @MockK
    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var articleFactory: ArticleFactory

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { viewModel } bind(MainViewModel::class)
                    single(qualifier=null) { articleFactory } bind(ArticleFactory::class)
                }
            )
        }

        subject = KeywordHighlighter()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun annotate() {
        val annotate = subject.invoke("It longs to ~~make~~ it.", "long")

        assertEquals(2, annotate.spanStyles.size)
        assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun boldingCase() {
        val annotate = subject.invoke("It **longs** to make it.", "long")

        assertEquals(2, annotate.spanStyles.size)
        assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun italicCase() {
        val annotate = subject.invoke("It longs ***to*** make it.", "long")

        assertEquals(2, annotate.spanStyles.size)
        assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun noopWithNull() {
        val annotate = subject.invoke("It longs to ~~make~~ it.", null)

        assertEquals(1, annotate.spanStyles.size)
        assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun link() {
        val annotate = subject.invoke("It [longs](https://www.yahoo.co.jp) to make it.", null)

        assertEquals(1, annotate.spanStyles.size)
        assertTrue(annotate.spanStyles.any { it.item.textDecoration == Underline })
        assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun multipleLink() {
        val annotate = subject.invoke("It [longs](https://www.yahoo.co.jp) to [make](https://www.make.it/sample) it.", null)

        assertEquals(2, annotate.spanStyles.size)
        assertTrue(annotate.spanStyles.any { it.item.textDecoration == Underline })
        assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun defaultArgs() {
        val annotate = subject.invoke("It [longs](https://www.yahoo.co.jp) to make it.")

        assertEquals(1, annotate.spanStyles.size)
        assertTrue(annotate.spanStyles.any { it.item.textDecoration == Underline })
        assertEquals("It longs to make it.", annotate.text)
    }

    @Test
    fun linkAndTextCase() {
        val annotate = subject.invoke(
            "- [Easter Egg in APK Files: What Is Frosting]" +
                    "(https://bi-zone.medium.com/easter-egg-in-apk-files-what-is-frosting-f356aa9f4d1)" +
                    "……いわゆる雪エフェクトの実装方法についてだった",
            "雪"
        )

        assertEquals(2, annotate.spanStyles.size)
        assertTrue(annotate.spanStyles.any { it.item.textDecoration == Underline })
        assertEquals(
            "- Easter Egg in APK Files: What Is Frosting……いわゆる雪エフェクトの実装方法についてだった",
            annotate.text
        )
    }

    @Test
    fun incorrectInput() {
        val annotate = subject.invoke(
            "## 『Dead recording』(2021,North Africa)",
            "Africa)"
        )

        assertTrue(annotate.spanStyles.isNotEmpty())
    }

}