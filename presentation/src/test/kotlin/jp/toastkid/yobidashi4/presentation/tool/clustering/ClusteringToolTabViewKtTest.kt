/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.tool.clustering

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path

class ClusteringToolTabViewKtTest {
    
    @RelaxedMockK
    private lateinit var viewModel: ClusteringToolTabViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        val element = mockk<Path>()
        every { element.fileName } returns element
        every { element.toString() } returns "test"
        every { viewModel.items() } returns listOf(element)
        every { viewModel.invoke(any()) } just Runs
        every { viewModel.dispose() } just Runs
        every { viewModel.remove(any()) } just Runs
        every { viewModel.listState() } returns LazyListState()
        coEvery { viewModel.collectDroppedPaths() } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun clusteringToolTabView() {
        runDesktopComposeUiTest {
            setContent {
                ClusteringToolTabView(viewModel)
            }

            onNodeWithText("x").performClick()
            verify { viewModel.remove(any()) }

            onNodeWithText("Invoke").performClick()
            verify { viewModel.invoke(any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun processingCase() {
        every { viewModel.processing() } returns true
        every { viewModel.openMarkdownPreview(any()) } just Runs
        every { viewModel.edit(any()) } just Runs
        every { viewModel.result() } returns mapOf("test" to listOf("Good"))

        runDesktopComposeUiTest {
            setContent {
                ClusteringToolTabView(viewModel)
            }

            onNode(hasContentDescription("Open preview")).performClick()
            verify { viewModel.openMarkdownPreview(any()) }

            onNode(hasContentDescription("Open file")).performClick()
            verify { viewModel.edit(any()) }
        }
    }

}
