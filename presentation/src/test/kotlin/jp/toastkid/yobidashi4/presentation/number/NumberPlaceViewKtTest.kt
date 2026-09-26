/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.number

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.MouseButton
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.number.NumberPlaceGame
import jp.toastkid.yobidashi4.domain.service.number.GameFileProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicBoolean

class NumberPlaceViewKtTest {

    @RelaxedMockK
    private lateinit var viewModel: NumberPlaceViewModel


    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Files::class)
        every { Files.size(any()) } returns 1
        mockkConstructor(GameFileProvider::class, NumberPlaceViewModel::class)
        every { anyConstructed<GameFileProvider>().invoke() } returns mockk()

        every { viewModel.openingMaskingCount() } returns false
        every { viewModel.openingDropdown() } returns false
        every { viewModel.openingCellOption(any(), any()) } returns false
        every { viewModel.openCellOption(any(), any()) } just Runs
        every { viewModel.place(any(), any(), any()) } just Runs
        every { viewModel.onCellLongClick(any(), any()) } just Runs
        every { viewModel.saveCurrentGame() } just Runs
        val numberPlaceGame = NumberPlaceGame()
        numberPlaceGame.initialize(20)
        every { viewModel.masked() } returns numberPlaceGame.masked()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun numberPlaceView() {
        runDesktopComposeUiTest {
            setContent {
                NumberPlaceView(viewModel)
            }

            onAllNodesWithContentDescription("Masked cell").onFirst()
                .assertExists("Not found!")
                .performMouseInput {
                    click()
                    longClick()
                }

            verify { viewModel.openCellOption(any(), any()) }
            verify { viewModel.onCellLongClick(any(), any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun mainOption() {
        every { viewModel.renewGame() } just Runs
        every { viewModel.setCorrect() } just Runs
        every { viewModel.clear() } just Runs
        every { viewModel.openingDropdown() } returns true

        runDesktopComposeUiTest {
            setContent {
                NumberPlaceView(viewModel)
            }

            onNode(hasText("Other board"), useUnmergedTree = true)
                .performMouseInput {
                    click()
                }
            verify { viewModel.renewGame() }

            onNode(hasText("Set answer"), useUnmergedTree = true)
                .performMouseInput {
                    click()
                }
            verify { viewModel.setCorrect() }

            onNode(hasText("Clear"), useUnmergedTree = true)
                .performMouseInput {
                    click()
                }
            verify { viewModel.clear() }

            onNodeWithContentDescription("Surface", useUnmergedTree = true)
                .performMouseInput {
                    press(MouseButton.Secondary)
                    release(MouseButton.Secondary)

                    press(MouseButton.Primary)
                    release()
                }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun maskingCount() {
        every { viewModel.openingMaskingCount() } returns true
        every { viewModel.setMaskingCount(any()) } just Runs
        every { viewModel.reloadGame() } just Runs
        every { viewModel.closeMaskingCount() } just Runs

        runDesktopComposeUiTest {
            setContent {
                NumberPlaceView(viewModel)
            }

            verify { viewModel.openingMaskingCount() }

            onNodeWithContentDescription("masking_count_10", useUnmergedTree = true).onParent()
                .assertExists("Not found!")
                .performClick()

            verify { viewModel.setMaskingCount(any()) }
            verify { viewModel.reloadGame() }
            verify { viewModel.closeMaskingCount() }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun withCellsDropdown() {
        val firstOnly = AtomicBoolean(true)
        every { viewModel.openingCellOption(any(), any()) } answers {
            val get = firstOnly.getAndSet(false)
            get
        }

        runDesktopComposeUiTest {
            setContent {
                NumberPlaceView(viewModel)
            }

            onAllNodesWithContentDescription("chooser_3", useUnmergedTree = true).onFirst()
                .assertExists("Not found!")
                .performClick()

            onAllNodesWithContentDescription("chooser_-1")
                .onFirst()
                .performClick()

            verify { viewModel.place(any(), any(), any()) }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun withLoading() {
        every { viewModel.loading() } returns true

        runDesktopComposeUiTest {
            setContent {
                NumberPlaceView(viewModel)
            }

            verify { viewModel.loading() }
        }
    }

}