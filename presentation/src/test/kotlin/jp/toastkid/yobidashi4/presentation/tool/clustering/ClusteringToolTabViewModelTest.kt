/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.tool.clustering

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.article.Article
import jp.toastkid.yobidashi4.domain.model.article.ArticleFactory
import jp.toastkid.yobidashi4.presentation.viewmodel.main.MainViewModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class ClusteringToolTabViewModelTest {

    private lateinit var subject: ClusteringToolTabViewModel

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

        every { viewModel.showSnackbar(any(), any(), any()) } just Runs
        every { viewModel.openFile(any()) } just Runs
        every { viewModel.registerDroppedPathReceiver(any()) } just Runs
        every { viewModel.unregisterDroppedPathReceiver() } just Runs
        every { viewModel.openPreview(any()) } just Runs
        every { viewModel.editWithTitle(any()) } just Runs
        val article = mockk<Article>()
        every { articleFactory.withTitle(any()) } returns article
        every { article.path() } returns mockk()

        subject = ClusteringToolTabViewModel()
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun processing() {
        assertFalse(subject.processing())
    }

    @Test
    fun result() {
        assertTrue(subject.result().isEmpty())
    }

    @Test
    fun listState() {
        assertEquals(0, subject.listState().firstVisibleItemIndex)
    }

    @Test
    fun items() {
        assertTrue(subject.items().isEmpty())
    }

    @Test
    fun invoke() {
    }

    @Test
    fun clearPaths() {
        subject.clearPaths()
    }

    @Test
    fun collectDroppedPaths() {
        subject.collectDroppedPaths()

        verify { viewModel.registerDroppedPathReceiver(any()) }
    }

    @Test
    fun dispose() {
        subject.dispose()

        verify { viewModel.unregisterDroppedPathReceiver() }
    }

    @Test
    fun remove() {
        subject.remove(mockk())
    }

    @Test
    fun openMarkdownPreview() {
        subject.openMarkdownPreview("test")

        verify { viewModel.openPreview(any()) }
    }

    @Test
    fun edit() {
        subject.edit("test")

        verify { viewModel.editWithTitle(any()) }
    }

}