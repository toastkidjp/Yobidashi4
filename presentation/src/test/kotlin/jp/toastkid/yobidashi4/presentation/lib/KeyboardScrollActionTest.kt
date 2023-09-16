package jp.toastkid.yobidashi4.presentation.lib

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalComposeUiApi::class)
class KeyboardScrollActionTest {

    @InjectMockKs
    private lateinit var keyboardScrollAction: KeyboardScrollAction

    @MockK
    private lateinit var state: ScrollableState

    @MockK
    private lateinit var lazyListState: LazyListState

    private lateinit var coroutineScope: CoroutineScope

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        coroutineScope = CoroutineScope(Dispatchers.Unconfined)

        coEvery { lazyListState.layoutInfo.totalItemsCount } returns 20
        coEvery { lazyListState.scrollToItem(any()) } just Runs

        mockkStatic("androidx.compose.foundation.gestures.ScrollExtensionsKt")
        coEvery { state.animateScrollBy(any()) } returns 0f
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun up() {
        keyboardScrollAction.invoke(coroutineScope, Key.DirectionUp, false)

        coVerify(exactly = 1) { state.animateScrollBy(any()) }
    }

    @Test
    fun down() {
        keyboardScrollAction.invoke(coroutineScope, Key.DirectionDown, false)

        coVerify(exactly = 1) { state.animateScrollBy(any()) }
    }

    @Test
    fun pageUp() {
        keyboardScrollAction.invoke(coroutineScope, Key.PageUp, false)

        coVerify(exactly = 1) { state.animateScrollBy(-300f) }
    }

    @Test
    fun pageDown() {
        keyboardScrollAction.invoke(coroutineScope, Key.PageDown, false)

        coVerify(exactly = 1) { state.animateScrollBy(300f) }
    }

    @Test
    fun toBottom() {
        keyboardScrollAction.invoke(coroutineScope, Key.DirectionDown, true)

        coVerify(exactly = 1) { state.animateScrollBy(Float.MAX_VALUE) }
    }

    @Test
    fun toBottomForLazyListStateCase() {
        keyboardScrollAction = KeyboardScrollAction(lazyListState)

        keyboardScrollAction.invoke(coroutineScope, Key.DirectionDown, true)

        coVerify(inverse = true) { state.animateScrollBy(any()) }
        coVerify(exactly = 1) { lazyListState.scrollToItem(20) }
    }

    @Test
    fun other() {
        keyboardScrollAction.invoke(coroutineScope, Key.DirectionRight, false)

        coVerify(inverse = true) { state.animateScrollBy(any(), any()) }
    }
}