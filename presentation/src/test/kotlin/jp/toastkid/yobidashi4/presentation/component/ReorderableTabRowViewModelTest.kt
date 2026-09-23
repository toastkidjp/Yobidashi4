package jp.toastkid.yobidashi4.presentation.component

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ReorderableTabRowViewModelTest {

    private lateinit var viewModel: ReorderableTabRowViewModel

    @BeforeEach
    fun setUp() {
        viewModel = ReorderableTabRowViewModel()
    }

    @Nested
    inner class InitialStateTests {
        @Test
        fun `initial state should have no active drag`() {
            assertFalse(viewModel.isDragStarted())
            val (isDragging, translationX) = viewModel.calculateForIndividualTab(0)
            assertFalse(isDragging)
            assertEquals(0f, translationX)
        }
    }

    @Nested
    inner class OnPressTests {
        @Test
        fun `drag should not start when total movement is below touchSlop`() {
            viewModel.onPress(index = 0, dragAmount = 5f, touchSlop = 10f)

            assertFalse(viewModel.isDragStarted())
            val (isDragging, _) = viewModel.calculateForIndividualTab(0)
            assertFalse(isDragging)
        }

        @Test
        fun `drag should start when total movement exceeds touchSlop`() {
            viewModel.onPress(index = 0, dragAmount = 15f, touchSlop = 10f)

            assertTrue(viewModel.isDragStarted())
            val (isDragging, offset) = viewModel.calculateForIndividualTab(0)
            assertTrue(isDragging)
            // Initial press: totalDragX(15) triggers start, sets dragOffset = 15, then adds dragAmount(15) = 30f
            assertEquals(30f, offset)
        }

        @Test
        fun `subsequent drag input should accumulate offset when drag is active`() {
            viewModel.onPress(index = 0, dragAmount = 15f, touchSlop = 10f)
            viewModel.onPress(index = 0, dragAmount = 10f, touchSlop = 10f)

            assertTrue(viewModel.isDragStarted())
            val (isDragging, offset) = viewModel.calculateForIndividualTab(0)
            assertTrue(isDragging)
            assertEquals(40f, offset)
        }
    }

    @Nested
    inner class CalculateForIndividualTabTests {

        @BeforeEach
        fun setupTabs() {
            viewModel.setTabWidth(0, 100f)
            viewModel.setTabWidth(1, 100f)
            viewModel.setTabWidth(2, 100f)
        }

        @Test
        fun `all tabs should have zero translation when no tab is being dragged`() {
            val (isDragging, translationX) = viewModel.calculateForIndividualTab(1)
            assertFalse(isDragging)
            assertEquals(0f, translationX)
        }

        @Test
        fun `the dragged tab should return its current dragOffset`() {
            viewModel.onPress(index = 1, dragAmount = 20f, touchSlop = 10f)

            val (isDragging, translationX) = viewModel.calculateForIndividualTab(1)
            assertTrue(isDragging)
            assertEquals(40f, translationX)
        }

        @Test
        fun `dragging right past threshold should shift subsequent tabs to the left`() {
            // Drag index 0 to the right past threshold
            viewModel.onPress(index = 0, dragAmount = 100f, touchSlop = 10f)

            // Index 1 should shift left (-draggedWidth)
            val (isDragging, translationX) = viewModel.calculateForIndividualTab(1)
            assertFalse(isDragging)
            assertEquals(-100f, translationX)

            // Index 2 threshold not met yet, should remain unchanged
            val (_, translationX2) = viewModel.calculateForIndividualTab(2)
            assertEquals(0f, translationX2)
        }

        @Test
        fun `dragging left past threshold should shift preceding tabs to the right`() {
            // Drag index 2 to the left past threshold
            viewModel.onPress(index = 2, dragAmount = -100f, touchSlop = 10f)

            // Index 1 should shift right (draggedWidth)
            val (isDragging, translationX) = viewModel.calculateForIndividualTab(1)
            assertFalse(isDragging)
            assertEquals(100f, translationX)
        }

        @Test
        fun `should fallback gracefully when tab widths are not recorded`() {
            val emptyViewModel = ReorderableTabRowViewModel()
            emptyViewModel.onPress(index = 0, dragAmount = 50f, touchSlop = 10f)

            val (_, translationX) = emptyViewModel.calculateForIndividualTab(1)
            assertEquals(0f, translationX)
        }
    }

}