/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.tool.clustering

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path

class ClusteringToolTabViewKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(ClusteringToolTabViewModel::class)
        val element = mockk<Path>()
        every { element.fileName } returns element
        every { element.toString() } returns "test"
        every { anyConstructed<ClusteringToolTabViewModel>().items() } returns listOf(element)
        every { anyConstructed<ClusteringToolTabViewModel>().dispose() } just Runs
        every { anyConstructed<ClusteringToolTabViewModel>().remove(any()) } just Runs
        coEvery { anyConstructed<ClusteringToolTabViewModel>().collectDroppedPaths() } just Runs
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
                ClusteringToolTabView()
            }

            onNodeWithText("x").performClick()
            verify { anyConstructed<ClusteringToolTabViewModel>().remove(any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun processingCase() {
        every { anyConstructed<ClusteringToolTabViewModel>().processing() } returns true
        every { anyConstructed<ClusteringToolTabViewModel>().result() } returns mapOf("test" to listOf("Good"))

        runDesktopComposeUiTest {
            setContent {
                ClusteringToolTabView()
            }
        }
    }
    //
}